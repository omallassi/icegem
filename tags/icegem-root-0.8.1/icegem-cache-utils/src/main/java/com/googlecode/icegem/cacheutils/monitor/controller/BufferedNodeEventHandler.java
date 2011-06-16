package com.googlecode.icegem.cacheutils.monitor.controller;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEvent;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;

/**
 * Contains the list of events since the last execution of the operation
 * getAndClearEventslist(),
 */
public class BufferedNodeEventHandler implements NodeEventHandler {

	private List<NodeEvent> eventslist = new ArrayList<NodeEvent>();

	/**
	 * Adds one more event to the buffer
	 */
	public void handle(NodeEvent event) {
		eventslist.add(event);
	}

	/**
	 * Gets the copy of the buffer and clears the buffer.
	 * 
	 * @return - the copy of events since the last call of this method.
	 */
	public List<NodeEvent> getAndClearEventslist() {
		ArrayList<NodeEvent> result = new ArrayList<NodeEvent>(eventslist);
		eventslist.clear();
		return result;
	}

}
