#!/bin/bash

set SERVER_CLASSPATH=.:lib/gemfire-${com.gemstone.gemfire.version}.jar:lib/mail-1.4.1.jar:lib/activation-1.1.jar:lib/log4j-${log4j.version}.jar
java -classpath $SERVER_CLASSPATH com.googlecode.icegem.cacheutils.monitor.MonitoringTool