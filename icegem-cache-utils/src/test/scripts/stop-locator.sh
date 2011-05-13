#!/bin/bash

java -cp ${gemfire.jar} com.gemstone.gemfire.internal.SystemAdmin stop-locator -port=10355 -Dgemfire.mcast-port=0 -dir=locator