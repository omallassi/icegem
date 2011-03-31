#!/bin/sh

# determine the java command to be run
JAVA=`which java` || { echo no java in PATH; exit 1 ; }


CLASSPATH1=$CLASSPATH:.
CLASSPATH1=$CLASSPATH1:./lib/commons-cli-1.2.jar
CLASSPATH1=$CLASSPATH1:./lib/commons-logging-1.1.1.jar
CLASSPATH1=$CLASSPATH1:./lib/commons-lang-2.5.jar
CLASSPATH1=$CLASSPATH1:./lib/icegem-core-0.1-SNAPSHOT.jar
CLASSPATH1=$CLASSPATH1:./lib/javassist-3.8.0.GA.jar
CLASSPATH1=$CLASSPATH1:./lib/slf4j-api-1.5.11.jar
CLASSPATH1=$CLASSPATH1:./lib/slf4j-jdk14-1.5.11.jar
CLASSPATH1=$CLASSPATH1:./lib/spring-core-3.0.3.RELEASE.jar


$JAVA -version || { echo  'java binary exits bad'; exit 2 ; }
$JAVA -cp $CLASSPATH1 com.griddynamics.icegem.cacheutils.updater.UpdateManager  $*
exit $?

