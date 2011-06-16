/**
 * 
 */
package com.googlecode.icegem.barrier;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gemstone.bp.edu.emory.mathcs.backport.java.util.Collections;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.FunctionService;

/**
 * MessageBarrierInputBean implements client side of PeerRegionListeningBarrierBean. 
 * 
 * <br/>
 * For each message injected to the barrier through {@link #process(Serializable)}, 
 * external strategy object {@link BarrierPolicy} is invoked to check whether message can pass the barrier.
 * <br/>
 * On positive decision, the message is sent to the destination. Otherwise, 
 * it goes to message store region and waits there until any of associated 
 * objects in the observed region change.
 * <br/>
 *  
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
public class MessageBarrierInputBean {

	private static final Logger log = LoggerFactory.getLogger(MessageBarrierInputBean.class);
	
	private static final long DEFAULT_BARRIER_TIMEOUT = 3600 * 1000;

	private String barrierName;
	
	private BarrierPolicy barrierPolicy;
	
	private Region<String, MessageHolder> messageRegion;
	
	private MessageDestination destination;
	
	private long barrierTimeoutMillis = DEFAULT_BARRIER_TIMEOUT;
	
	public void process(Serializable message) {
		String id = this.barrierPolicy.getMessageId(message);
		log.debug("Barrier {} processing message {}", this.barrierName, id);
		if(this.barrierPolicy.canProceed(message)) {
			destination.send(barrierName, message);
			log.debug("Barrier {} passed message {}", this.barrierName, id);
		} else {
			long ctime = System.currentTimeMillis();
			List<Serializable> trackedEntities = this.barrierPolicy.getTrackedEntities(message);
			
			MessageHolder holder = new MessageHolder(this.barrierName, id, message);
			
			holder.setCreateTime(ctime);
			holder.setTimeoutTime(ctime + barrierTimeoutMillis);
			holder.setTrackedEntityKeys(trackedEntities);

			sendToGrid(holder);
			
			log.debug("Barrier {} holded off message {}. Timeout {}", new Object[] {this.barrierName, id, this.barrierTimeoutMillis});
		}
	}

	@Required
	public void setBarrierName(String barrierName) {
		this.barrierName = barrierName;
	}

	@Required
	public void setBarrierPolicy(BarrierPolicy barrierPolicy) {
		this.barrierPolicy = barrierPolicy;
	}

	public void setBarrierTimeoutMillis(long barrierTimeoutMillis) {
		this.barrierTimeoutMillis = barrierTimeoutMillis;
	}
	
	@Required
	public void setDestination(MessageDestination destination) {
		this.destination = destination;
	}

	@Required
	public void setMessageRegion(Region<String, MessageHolder> messageRegion) {
		this.messageRegion = messageRegion;
	}

	private void sendToGrid(MessageHolder holder) {
		Execution execution = FunctionService.onRegion(messageRegion);
		execution.withArgs(new Object[] {messageRegion.getName(), holder});
		execution.withFilter(Collections.singleton(holder.getKey()));
		execution.execute(FunctionService.getFunction(MessageSubmitFunction.FUNCTION_ID));
	}
}
