/**
 * 
 */
package com.googlecode.icegem.barrier;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;

/**
 * This is function used to submit delayed messages to data-grid. 
 * 
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
public class MessageSubmitFunction extends FunctionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String FUNCTION_ID = MessageSubmitFunction.class.getName();
	
	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.FunctionAdapter#execute(com.gemstone.gemfire.cache.execute.FunctionContext)
	 */
	@Override
	public void execute(FunctionContext ctx) {
		Object[] args = (Object[]) ctx.getArguments();
		if(args.length != 2) {
			throw new IllegalArgumentException();
		}
		String regionName = (String) args[0];
		MessageHolder holder = (MessageHolder) args[1];
		
		Cache cache = CacheFactory.getAnyInstance();
		Region<String, MessageHolder> region = cache.getRegion(regionName);
		
		// HACK:Rely that server-side message barrier bean has set itself as a userAttribute of region.
		// This is the only way to get it without usage of even more nasty static singletons.
		PeerRegionListeningBarrierBean barrierBean = (PeerRegionListeningBarrierBean) region.getUserAttribute();
		
		barrierBean.receiveNewMessage(holder, ctx.isPossibleDuplicate());
	}

	@Override
	public boolean hasResult() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.FunctionAdapter#getId()
	 */
	@Override
	public String getId() {
		return FUNCTION_ID;
	}

	@Override
	public boolean optimizeForWrite() {
		// Run on primary bucket holders only
		return true;
	}
	
	@Override
	public boolean isHA() {
		// Can retry on primary bucket failure
		return true;
	}
}
