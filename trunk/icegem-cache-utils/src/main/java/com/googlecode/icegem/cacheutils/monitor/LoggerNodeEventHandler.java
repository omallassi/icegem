package com.googlecode.icegem.cacheutils.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEvent;
import com.googlecode.icegem.cacheutils.monitor.controller.event.NodeEventHandler;

/**
 * Writes log record in case of event
 */
public class LoggerNodeEventHandler implements NodeEventHandler {
	private static final Logger log = LoggerFactory.getLogger(LoggerNodeEventHandler.class);

	public void handle(NodeEvent event) {
		log.info(event.toString());
	}

}
