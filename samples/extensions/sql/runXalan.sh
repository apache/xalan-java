#!/bin/sh
#
#=========================================================================
# Copyright 2004 The Apache Software Foundation.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0

if [ "$DERBY_JAR_DIR" = "" ] ; then
    DERBYJAR_DIR=.
fi


_JAVACMD=$JAVA_HOME/bin/java

_BUILD_DIR=../../../build

_JAR_DIR=../../../lib


_ENDORSED_DIR=$_BUILD_DIR

if [ "$JAVA_HOME" = "" ] ; then
    echo "Warning: JAVA_HOME environment variable is not set."
    _JAVACMD=java
fi

if [ "$PARSER_JAR" = "" ] ; then
    PARSER_JAR=$_JAR_DIR/xercesImpl.jar
fi

if [ "$XML_APIS_JAR" = "" ] ; then
    XML_APIS_JAR=$_JAR_DIR/xml-apis.jar
fi

if [ "$XALAN_JAR" = "" ] ; then
    XALAN_JAR=$_BUILD_DIR/xalan.jar
fi


# Use _underscore prefix to not conflict with user's settings
# Default to UNIX-style pathing
CLPATHSEP=:
# if we're on a Windows box make it ;
uname | grep WIN && CLPATHSEP=\;

_CLASSPATH="$XALAN_JAR${CLPATHSEP}$XML_APIS_JAR${CLPATHSEP}$PARSER_JAR${CLPATHSEP}$XALAN_JAR${CLPATHSEP}$DERBYJAR_DIR/derby.jar${CLPATHSEP}$DERBYJAR_DIR/derbytools.jar${CLPATHSEP}$CLASSPATH"

# Attempt to automatically add system classes to _CLASSPATH
if [ -f $JAVA_HOME/lib/tools.jar ] ; then
  _CLASSPATH=${_CLASSPATH}${CLPATHSEP}${JAVA_HOME}/lib/tools.jar
fi

if [ -f $JAVA_HOME/lib/classes.zip ] ; then
  _CLASSPATH=${_CLASSPATH}${CLPATHSEP}${JAVA_HOME}/lib/classes.zip
fi


echo "Running Derby: $@"
echo "        ...with classpath: $_CLASSPATH"

"$_JAVACMD" $JAVA_OPTS -Djava.endorsed.dirs=$_ENDORSED_DIR -classpath "$_CLASSPATH" -Dij.protocol=jdbc:derby: org.apache.xalan.xslt.Process $@




