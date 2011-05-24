package com.googlecode.icegem.cacheutils.monitor.controller.event;

/**
 * Interface which should be implemented to handle the node events.
 */
public interface NodeEventHandler {

	/**
	 * Handle the node event
	 * 
	 * @param event
	 *            - the node event
	 */
	void handle(NodeEvent event);

}
