package com.googlecode.icegem.cacheutils.monitor.server;

import java.io.IOException;

import com.gemstone.gemfire.cache.CacheFactory;

/**
 * The very simple realization of the cache server
 */
public class Server {

	public Server() {
		initCache();
	}

	private void initCache() {
		new CacheFactory().set("cache-xml-file", "server.xml").create();
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}
}
