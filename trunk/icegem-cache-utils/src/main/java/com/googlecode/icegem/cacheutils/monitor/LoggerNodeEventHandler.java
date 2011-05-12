package com.googlecode.icegem.cacheutils.monitor;

import org.apache.log4j.Logger;

import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEvent;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;

/**
 * Writes log record in case of event
 */
public class LoggerNodeEventHandler implements NodeEventHandler {
	private static final Logger log = Logger.getLogger(LoggerNodeEventHandler.class);

	public void handle(NodeEvent event) {
		log.info(event.toString());
	}

}
