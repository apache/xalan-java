#!/bin/sh
#	Name:   build.sh Build Xalan-J 2.x using Ant
#	Author: Shane Curcuru

echo "Xalan-J 2.x Build"
echo "-------------"

_JAVACMD=$JAVA_HOME/bin/java
if [ "$JAVA_HOME" = "" ] ; then
    echo "Warning: JAVA_HOME environment variable is not set."
    _JAVACMD=java
fi

# Default locations of jars we depend on to run Ant on our build.xml file
if [ "$ANT_HOME" = "" ] ; then
    ANT_HOME=.
fi
if [ "$ANT_JAR" = "" ] ; then
    ANT_JAR=./bin/ant.jar
fi
if [ "$PARSER_JAR" = "" ] ; then
    PARSER_JAR=./bin/xerces.jar
fi

# Default to UNIX-style pathing
CLPATHSEP=:
# if we're on a Windows box make it ;
uname | grep WIN && CLPATHSEP=\;
_CLASSPATH="$ANT_JAR${CLPATHSEP}$PARSER_JAR${CLPATHSEP}$CLASSPATH"

echo "Starting Ant with targets: $@"
echo "        ...with classpath: $_CLASSPATH"

"$_JAVACMD" $JAVA_OPTS -Dant.home=$ANT_HOME -classpath "$_CLASSPATH" org.apache.tools.ant.Main $@




