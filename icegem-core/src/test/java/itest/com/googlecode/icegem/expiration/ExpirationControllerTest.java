package itest.com.googlecode.icegem.expiration;

import com.googlecode.icegem.utils.RegionUtils;
import itest.com.googlecode.icegem.expiration.model.Transaction;
import itest.com.googlecode.icegem.expiration.model.TransactionProcessingError;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

import org.fest.assertions.Assertions;
import org.testng.annotations.*;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.Region.Entry;
import com.gemstone.gemfire.cache.RegionService;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.googlecode.icegem.expiration.ExpirationController;
import com.googlecode.icegem.expiration.ExpirationPolicy;
import com.googlecode.icegem.utils.JavaProcessLauncher;
import com.googlecode.icegem.utils.ServerTemplate;

/**
 * <p>
 * Tests the ExpirationController for the following scenario:
 * <ol>
 * <li>Fill the transactions region with 5 transactions in different states</li>
 * <li>Fill the errors region with 2 errors associated with 2 last transactions</li>
 * <li>Wait 3 seconds (it is more than 1 second of the expiration time)</li>
 * <li>Create and run the ExpirationController which configured with
 * TransactionExpirationPolicy and can be recursive or not</li>
 * <ul>
 * <li>If recursive = true than in case of the transaction is expired we will
 * destroy also error associated with the transaction</li>
 * <li>If recursive = false than delete only transactions</li>
 * </ul>
 * </ol>
 * </p>
 */
public class ExpirationControllerTest implements Serializable {

	private static final long serialVersionUID = -1467927314327799826L;

	private static final long EXPIRATION_TIME = 1 * 1000;

	/** Field cacheServer1 */
	private static Process cacheServer1;
	/** Field cacheServer2 */
	private static Process cacheServer2;
	/** Field javaProcessLauncher */
	private static JavaProcessLauncher javaProcessLauncher = new JavaProcessLauncher();

	private class TransactionExpirationPolicy implements ExpirationPolicy {

		private static final long serialVersionUID = -8642198262421835809L;

		private long expirationTime;
		private boolean recursively;

		public TransactionExpirationPolicy(long expirationTime,
			boolean recursively) {
			this.expirationTime = expirationTime;
			this.recursively = recursively;
		}

		private boolean isTimeExpired(long finishedAt) {
			boolean timeExpired = false;

			long checkedAt = System.currentTimeMillis();
			long idleDuration = checkedAt - finishedAt;

			if (idleDuration > expirationTime) {
				timeExpired = true;
			}

			return timeExpired;
		}

		public boolean isExpired(Entry<Object, Object> entry) {

			boolean expired = false;

			Object key = entry.getKey();
			Object value = entry.getValue();

			if ((key instanceof Long) && (value instanceof Transaction)) {
				Long transactionId = (Long) key;
				Transaction transaction = (Transaction) value;

				boolean timeExpired = isTimeExpired(transaction.getFinishedAt());

				RegionService regionService = entry.getRegion()
					.getRegionService();
				Region<Long, TransactionProcessingError> errorsRegion = regionService
					.getRegion("errors");

				if (transaction.isProcessedSuccessfully() && timeExpired) {
					expired = true;
				} else {
					TransactionProcessingError error = errorsRegion
						.get(transactionId);

					if (error != null) {
						timeExpired = isTimeExpired(error.getResolvedAt());
						if (error.isResolved() && timeExpired) {
							expired = true;
						}
					}
				}

				if (expired && recursively) {
					TransactionProcessingError error = errorsRegion
						.get(transactionId);
					if (error != null) {
						errorsRegion.destroy(transactionId);
					}
				}

			}

			return expired;
		}

	}

	@BeforeClass
	public void setUp() throws IOException, InterruptedException,
		TimeoutException {
		startCacheServers();
	}

	@AfterClass
	public void tearDown() throws IOException, InterruptedException {
		stopCacheServers();
	}

	@Test
	public void testProcessNotRecursive() throws Throwable {
		fillData();
		assertThat(5, 2);
		Thread.sleep(3 * 1000);
		long destroyedEntriesNumber = expire(false);
		Assertions.assertThat(destroyedEntriesNumber).isEqualTo(2);
		assertThat(3, 2);
	}

	@Test
	public void testProcessRecursive() throws InterruptedException {
		fillData();
		assertThat(5, 2);
		Thread.sleep(3 * 1000);

		long destroyedEntriesNumber = expire(true);
		Assertions.assertThat(destroyedEntriesNumber).isEqualTo(2);
		assertThat(3, 1);
	}

	@Test
	public void testProcessLoad() throws InterruptedException {
		System.out.println("Smart expiration load test start");
		System.out.println("Before fillData");
		final int count = 10000;
		long startTime = System.currentTimeMillis();
		fillData(count);
		long finishTime = System.currentTimeMillis();
		System.out.println("Data filled in " + (finishTime - startTime) + "ms");
		assertThat(5 * count, 2 * count);
		Thread.sleep(3 * 1000);

		System.out.println("Before expire");
		startTime = System.currentTimeMillis();
		long destroyedEntriesNumber = expire(false, 1000, 1000);
		finishTime = System.currentTimeMillis();
		System.out.println("Expired in " + (finishTime - startTime) + "ms");
		Assertions.assertThat(destroyedEntriesNumber).isEqualTo(2 * count);
		assertThat(3 * count, 2 * count);
		System.out.println("Smart expiration load test finish");
	}

	private <K, V> Region<K, V> getRegion(ClientCache cache, String regionName) {
		ClientRegionFactory<K, V> clientRegionFactory = cache
			.createClientRegionFactory(ClientRegionShortcut.PROXY);

		Region<K, V> region = cache.getRegion(regionName);

		if (region == null) {
			region = clientRegionFactory.create(regionName);
		}

		return region;
	}

	private void fillData() {
		fillData(1);
	}

	private void fillData(long count) {
		ClientCache cache = new ClientCacheFactory()
			.addPoolLocator("localhost", 10355).set("log-level", "warning")
			.create();

		Region<Long, Transaction> transactionsRegion = getRegion(cache,
			"transactions");
        RegionUtils.clearRegion(transactionsRegion);
		Region<Long, TransactionProcessingError> errorsRegion = getRegion(
			cache, "errors");
        RegionUtils.clearRegion(errorsRegion);

		for (long i = 1, id = 1; i <= count; i++, id += 5) {
			if ((i % 1000) == 0) {
				System.out.println("Filling cycle number " + i);
			}

			Transaction notStartedTransaction = new Transaction();
			transactionsRegion.put(id, notStartedTransaction);

			Transaction startedTransaction = new Transaction();
			startedTransaction.begin();
			transactionsRegion.put(id + 1, startedTransaction);

			Transaction committedTransaction = new Transaction();
			committedTransaction.begin();
			committedTransaction.commit();
			transactionsRegion.put(id + 2, committedTransaction);

			Transaction rolledbackUnresolvedTransaction = new Transaction();
			rolledbackUnresolvedTransaction.begin();
			rolledbackUnresolvedTransaction.rollback();
			transactionsRegion.put(id + 3, rolledbackUnresolvedTransaction);

			Transaction rolledbackResolvedTransaction = new Transaction();
			rolledbackResolvedTransaction.begin();
			rolledbackResolvedTransaction.rollback();
			transactionsRegion.put(id + 4, rolledbackResolvedTransaction);

			TransactionProcessingError unresolvedError = new TransactionProcessingError(
				"Error during the transaction processing");
			errorsRegion.put(id + 3, unresolvedError);

			TransactionProcessingError resolvedError = new TransactionProcessingError(
				"Error during the transaction processing");
			resolvedError.setResolved();
			errorsRegion.put(id + 4, resolvedError);
		}

		cache.close();
	}

	private void assertThat(int transactionsNumber, int errorsNumber) {
		ClientCache cache = new ClientCacheFactory()
			.addPoolLocator("localhost", 10355).set("log-level", "warning")
			.create();

		Region<Long, Transaction> transactionsRegion = getRegion(cache,
			"transactions");

		Region<Long, TransactionProcessingError> errorsRegion = getRegion(
			cache, "errors");

		Assertions.assertThat(transactionsRegion.keySetOnServer().size())
			.isEqualTo(transactionsNumber);
		Assertions.assertThat(errorsRegion.keySetOnServer().size()).isEqualTo(
			errorsNumber);

		cache.close();
	}

	private long expire(boolean recursively, long packetSize, long packetDelay) {
		ExpirationController expirationController = new ExpirationController(
			"127.0.0.1", 10355);

		long destroyedEntriesNumber = expirationController.process(
			"transactions", new TransactionExpirationPolicy(EXPIRATION_TIME,
				recursively), packetSize, packetDelay);

		expirationController.close();

		return destroyedEntriesNumber;
	}

	private long expire(boolean recursively) {
		return expire(recursively, 1, 0);
	}

	private void startCacheServers() throws IOException, InterruptedException {
        cacheServer1 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=expirationServerProperties.properties"}, null);
        cacheServer2 = javaProcessLauncher.runWithConfirmation(
                ServerTemplate.class, new String[]{"-DgemfirePropertyFile=expirationServerProperties.properties"}, null);
	}

	private void stopCacheServers() throws IOException, InterruptedException {
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer1);
		javaProcessLauncher.stopBySendingNewLineIntoProcess(cacheServer2);
	}

}
