#!/bin/bash

set SERVER_CLASSPATH=.:lib/gemfire-${com.gemstone.gemfire.version}.jar:lib/icegem-core-${version}.jar
java -DgemfirePropertyFile=gemfire-monitor.properties -classpath $SERVER_CLASSPATH com.googlecode.icegem.cacheutils.monitor.server.Server