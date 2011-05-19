#!/bin/bash

$GEMFIRE/bin/gemfire start-locator -port=10355 -Dgemfire.mcast-port=0 -dir=locator -properties=locator.properties