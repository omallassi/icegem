package com.googlecode.icegem.barrier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;

import com.gemstone.gemfire.CopyHelper;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.InterestResultPolicy;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionAttributes;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;
import com.googlecode.icegem.utils.OperationRequireRetryException;
import com.googlecode.icegem.utils.OperationRetryFailedException;
import com.googlecode.icegem.utils.RegionUtils;
import com.googlecode.icegem.utils.Retryable;

/**
 * PeerRegionListeningBarrierBean implements reliable message barrier able to
 * run in several processes connected through GemFire. <br/>
 * Messages are injected through {@link MessageBarrierInputBean} counterpart. If
 * the first decision is negative, messages go to messagestore region and
 * controlled by the bean since than. <br/>
 * The region runs a background jobs those used to ensure recovery from false
 * negative decision on a message due data race or barrier timeout. <br/>
 * The component optimized based on following assumptions:
 * <ul>
 * <li>Probability of message delay is much much lower than probability of
 * pass-through</li>
 * <li>Total number of messages to delay relatively small, so mapping fits to
 * local region</li>
 * <li>Probability two messages depend on the same state objects is small -
 * optimistic locking is used</li>
 * </ul>
 * *
 * 
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 * @author akondratyev
 * @version $Id$
 */
public class PeerRegionListeningBarrierBean implements InitializingBean,
		DisposableBean {

	/**
	 * RegionObserver is a {@link CacheListenerAdapter} which asynchronously
	 * fires reprocessing of messages associated with the event.
	 * 
	 * @author akondratyev
	 * @version $Id$
	 */
	private class RegionObserver extends
			CacheListenerAdapter<Serializable, Object> {

		/**
		 * @param event
		 * @see com.gemstone.gemfire.cache.util.CacheListenerAdapter#afterCreate(com.gemstone.gemfire.cache.EntryEvent)
		 */
		@Override
		public void afterCreate(EntryEvent<Serializable, Object> event) {
			asyncReprocessMessages(event.getKey());
		}

		/**
		 * @param event
		 * @see com.gemstone.gemfire.cache.util.CacheListenerAdapter#afterDestroy(com.gemstone.gemfire.cache.EntryEvent)
		 */
		@Override
		public void afterDestroy(EntryEvent<Serializable, Object> event) {
			asyncReprocessMessages(event.getKey());
		}

		/**
		 * @param event
		 * @see com.gemstone.gemfire.cache.util.CacheListenerAdapter#afterUpdate(com.gemstone.gemfire.cache.EntryEvent)
		 */
		@Override
		public void afterUpdate(EntryEvent<Serializable, Object> event) {
			asyncReprocessMessages(event.getKey());
		}
	}

	/**
	 * RetryTouchAndTimeoutTask is a background job which periodically checks
	 * all holded messages. The job shall pick up messages escaped due
	 * asynchronous delivery of change notifications and remove timed out
	 * messages.
	 * 
	 * @author akondratyev
	 * @version $Id$
	 */
	private class RetryTouchAndTimeoutTask implements Runnable {
		public void run() {
			logger.debug("Start local message store recycle");
			long processed = 0;
			for (MessageHolder h : messageRegion.values()) {
				checkMessage(h);

				if ((processed % messageBatchSize) == 0) {
					try {
						Thread.sleep(messageBatchDelay);
					} catch (InterruptedException e) {
						logger.info(
								"Message recycle task has been interrupted", e);
						break;
					}
				}

				if (Thread.interrupted()) {
					logger.info("Message recycle task has been interrupted");
					break;
				}
			}
			logger.debug("Finished retry and touch pass");
		}
	}

	private static final Logger logger = LoggerFactory
			.getLogger(PeerRegionListeningBarrierBean.class);

	private BarrierPolicy barrierPolicy;

	private MessageDestination destination;

	private Region<Serializable, List<String>> mappingRegion;
	private Region<String, MessageHolder> messageRegion;
	private Region<Serializable, Object> observedRegion;

	private int messageBatchSize = 1000;

	private int messageBatchDelay = 1000;

	private long messageCheckInterval = 900000;

	private TaskScheduler taskScheduler;

	private RegionObserver observer;
	
	private boolean started;
	
	private boolean autoStart;

	private int mappingUpdateMaxRetries = 5; 

	public boolean isStarted() {
		return started;
	}

	public boolean isAutoStart() {
		return autoStart;
	}

	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}

	public PeerRegionListeningBarrierBean() {
	}

	@SuppressWarnings("unchecked")
	private void addMessageToMapping(final List<Serializable> entityKeys,
			final String messageKey) {
		for (final Serializable entityKey : entityKeys) {
			try {
				RegionUtils.retryWithExponentialBackoff(new Retryable<Boolean>() {
					public Boolean execute() throws OperationRequireRetryException,
							InterruptedException {
						List<String> oldMessageIds;
						List<String> newMessageIds;
						
						// We rely on region cloning mechanism here
						oldMessageIds = mappingRegion.get(entityKey);

						if (oldMessageIds == null) {
							newMessageIds = new ArrayList<String>();
						} else {
							newMessageIds = (List<String>) CopyHelper.copy(oldMessageIds);
						}

						if (!newMessageIds.contains(messageKey)) {
							newMessageIds.add(messageKey);
						}

						if(!mappingRegion.replace(entityKey, oldMessageIds, newMessageIds)) {
							throw new OperationRequireRetryException("Replace in mapping region failed");
						}
						return Boolean.TRUE;
					}
				}, mappingUpdateMaxRetries);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (OperationRetryFailedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @throws Exception
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		validateRegions();
		initRegionContexts();
		initRegionHooks();
		initBackgroundJobs();
	}

	/**
	 * asynchronous execution of message processing to protect from deadlocks.
	 * 
	 * @param entityKey
	 */
	protected void asyncReprocessMessages(final Serializable entityKey) {
		TaskExecutor executor = (TaskExecutor) taskScheduler;
		executor.execute(new Runnable() {
			public void run() {
				String oldThreadName = Thread.currentThread().getName();
				try {
					String newName = "Message barrier - Process associated entries - "
							+ entityKey.toString();
					Thread.currentThread().setName(newName);

					reprocessAssociatedMessages(entityKey);
				} finally {
					Thread.currentThread().setName(oldThreadName);
				}
			}
		});
	}

	/**
	 * Check message and send it out if timed out or canProceed.
	 * 
	 * @param holder
	 *            the message holder object
	 */
	private void checkMessage(MessageHolder holder) {
		boolean canProceed = barrierPolicy.canProceed(holder.getPayload());
		boolean timedOut = holder.getTimeoutTime() <= System
				.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("Checking message {}: canProceed={}, timedOut={}",
					new Object[] { holder.getKey(), canProceed, timedOut });
		}

		if (canProceed || timedOut) {
			destination.send(holder.getBarrierName(), holder.getPayload());

			removeMessage(holder);
			logger.debug("Sent out message {}", holder.getKey());
		}
	}

	private void deleteMessageFromMapping(final List<Serializable> entityKeys,
			final String messageId) {
		for (final Serializable entityKey : entityKeys) {
			try {
				RegionUtils.retryWithExponentialBackoff(new Retryable<Boolean>() {
					public Boolean execute() throws OperationRequireRetryException,
							InterruptedException {
						List<String> newSequence;
						// rely on cloning here
						List<String> oldSequence = mappingRegion.get(entityKey);

						if (oldSequence == null) {
							logger.warn("Entity key {} doesn't present in mapping region",
									entityKey);
							return Boolean.FALSE;
						}
						newSequence = new ArrayList<String>(oldSequence);
						newSequence.remove(messageId);

						logger.debug("Removed {} from region", messageId);
						boolean success = false;
						
						if (newSequence.isEmpty()) {
							success = mappingRegion.remove(entityKey, oldSequence);
						} else {
							success = mappingRegion.replace(entityKey, oldSequence, newSequence);
						}
						
						if(!success) {
							throw new OperationRequireRetryException("deleteMessageFromMapping failed and need retry");
						}
						return Boolean.TRUE;
					}
				}, this.mappingUpdateMaxRetries);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (OperationRetryFailedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @throws Exception
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
	}

	public BarrierPolicy getBarrierPolicy() {
		return barrierPolicy;
	}

	public long getMessageCheckInterval() {
		return messageCheckInterval;
	}

	protected void initBackgroundJobs() {
		taskScheduler.scheduleWithFixedDelay(new RetryTouchAndTimeoutTask(),
				messageCheckInterval);
		logger.info("Barrier shall recycle messages every {} msec",
				messageCheckInterval);
	}

	private void initRegionContexts() {
		// This is used by MessageSubmitFunction to get reference to the bean.
		this.messageRegion.setUserAttribute(this);
	}

	private void initRegionHooks() {
		// Validate
		this.observer = new RegionObserver();
		this.observedRegion.getAttributesMutator().addCacheListener(
				this.observer);

		// TODO: Implement 
		// Ensure we run partitioned region listener here
	}

	public void receiveNewMessage(MessageHolder messageHolder,
			boolean possibleDuplicate) {
		if (possibleDuplicate && messageRegion.containsKey(messageHolder)) {
			logger.debug("Message {} already present in region",
					messageHolder.getKey());
		} else {
			messageRegion.put(messageHolder.getKey(), messageHolder);
			logger.debug("Stored message {}", messageHolder.getKey());
		}
		addMessageToMapping(messageHolder.getTrackedEntityKeys(),
				messageHolder.getKey());
	}

	private void removeMessage(MessageHolder holder) {
		String id = holder.getPayloadMessageId();

		String holderId = holder.getKey();
		messageRegion.remove(holderId);

		List<Serializable> entities = holder.getTrackedEntityKeys();
		deleteMessageFromMapping(entities, id);
	}

	/**
	 * @param key
	 */
	protected void reprocessAssociatedMessages(Serializable key) {
		List<String> messageIds = mappingRegion.get(key);

		if (messageIds != null) {
			for (String messageId : messageIds) {
				MessageHolder holder = messageRegion.get(messageId);
				if (holder != null) {
					checkMessage(holder);
				} else {
					logger.warn(
							"Mapping region references message {} which does not exists",
							messageId);
				}
			}
		}
	}

	@Required
	public void setBarrierPolicy(BarrierPolicy barrierPolicy) {
		this.barrierPolicy = barrierPolicy;
	}

	/**
	 * Set the mappingRegion.
	 * 
	 * @param mappingRegion
	 *            the mappingRegion
	 */
	@Required
	public void setMappingRegion(
			Region<Serializable, List<String>> messageSequenceRegion) {
		mappingRegion = messageSequenceRegion;
	}

	/**
	 * Set the messageBatchDelay.
	 * 
	 * @param messageBatchDelay
	 *            the messageBatchDelay
	 */
	public void setMessageBatchDelay(int messageBatchDelay) {
		this.messageBatchDelay = messageBatchDelay;
	}

	/**
	 * Set the messageBatchSize.
	 * 
	 * @param messageBatchSize
	 *            the messageBatchSize
	 */
	public void setMessageBatchSize(int messageBatchSize) {
		this.messageBatchSize = messageBatchSize;
	}

	/**
	 * Set the messageCheckInterval.
	 * 
	 * @param messageCheckInterval
	 *            the messageCheckInterval
	 */
	public void setMessageCheckInterval(long messageTouchInterval) {
		this.messageCheckInterval = messageTouchInterval;
	}

	/**
	 * Set the messageRegion.
	 * 
	 * @param region
	 *            the messageRegion
	 */
	@Required
	public void setMessageRegion(Region<String, MessageHolder> region) {
		messageRegion = region;
	}

	@Required
	public void setObservedRegion(Region<Serializable, Object> region) {
		observedRegion = region;
		observedRegion.registerInterest("ALL_KEYS", InterestResultPolicy.KEYS);
	}

	/**
	 * Set the taskScheduler.
	 * 
	 * @param taskScheduler
	 *            the taskScheduler
	 */
	@Required
	public void setTaskScheduler(TaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	private void validateRegions() {
		RegionAttributes<Serializable, List<String>> attrs = this.mappingRegion
				.getAttributes();
		if (!attrs.getCloningEnabled()) {
			throw new IllegalArgumentException(
					"Mapping region must have clonning enabled");
		}
		if (!attrs.getScope().isLocal()) {
			throw new IllegalArgumentException("Mapping region must be local");
		}
	}
}
