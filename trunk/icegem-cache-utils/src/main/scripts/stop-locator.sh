#!/bin/bash

java -cp lib/gemfire-${com.gemstone.gemfire.version}.jar com.gemstone.gemfire.internal.SystemAdmin stop-locator -port=10355 -Dgemfire.mcast-port=0