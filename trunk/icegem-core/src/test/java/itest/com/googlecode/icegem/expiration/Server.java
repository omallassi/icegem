package itest.com.googlecode.icegem.expiration;

import itest.com.googlecode.icegem.expiration.model.Transaction;
import itest.com.googlecode.icegem.expiration.model.TransactionProcessingError;

import java.io.IOException;

import com.gemstone.gemfire.cache.AttributesFactory;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.DataPolicy;
import com.gemstone.gemfire.cache.RegionAttributes;
import com.gemstone.gemfire.cache.server.CacheServer;
import com.googlecode.icegem.utils.CacheUtils;
import com.googlecode.icegem.utils.ConsoleUtils;
import com.googlecode.icegem.utils.JavaProcessLauncher;

/**
 * A simple cache server that stores partition region data. Use
 * JavaProcessLauncher to launch this cache server from tests.
 * 
 * @see JavaProcessLauncher
 * 
 * @author Andrey Stepanov aka standy
 */
public class Server {
	/** Field cache */
	private static Cache cache;
	/** Field LOCATOR_PORT */
	private static final int LOCATOR_PORT = 10355;

	/**
	 * Server entry point.
	 * 
	 * @param args
	 *            of type String[]
	 * @throws Exception
	 *             when
	 */
	public static void main(String[] args) throws IOException,
		InterruptedException {
		startCacheServer(CacheUtils.startLocator(args));

		System.out.println("Cache Server has been started");

		ConsoleUtils
			.waitForEnter(JavaProcessLauncher.PROCESS_STARTUP_COMPLETED);

		cache.close();

		System.err.println("Cache Server has been stopped");
	}

	/**
	 * Starts a cache server and locator in case of startLocator parameter is
	 * true.
	 * 
	 * @param startLocator
	 *            - if true than start locator
	 * @throws IOException
	 */
	public static void startCacheServer(boolean startLocator)
		throws IOException {
		CacheFactory cacheFactory = new CacheFactory().set("mcast-port", "0")
			.set("log-level", "warning");

		if (startLocator) {
			cacheFactory
				.set("start-locator", "localhost[" + LOCATOR_PORT + "]")
				.create();
		} else {
			cacheFactory.set("locators", "localhost[" + LOCATOR_PORT + "]");
		}

		cache = cacheFactory.create();

		AttributesFactory<Long, Transaction> transactionsAttributesFactory = new AttributesFactory<Long, Transaction>();
		transactionsAttributesFactory.setDataPolicy(DataPolicy.PARTITION);
		RegionAttributes<Long, Transaction> transactionsRegionAttributes = transactionsAttributesFactory
			.create();
		cache.createRegionFactory(transactionsRegionAttributes).create(
			"transactions");

		AttributesFactory<Long, TransactionProcessingError> errorsAttributesFactory = new AttributesFactory<Long, TransactionProcessingError>();
		errorsAttributesFactory.setDataPolicy(DataPolicy.PARTITION);
		RegionAttributes<Long, TransactionProcessingError> errorsRegionAttributes = errorsAttributesFactory
			.create();
		cache.createRegionFactory(errorsRegionAttributes).create("errors");

		CacheServer cacheServer = cache.addCacheServer();
		cacheServer.setPort(0);
		cacheServer.start();
	}

}
