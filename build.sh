#!/bin/sh
#	Name:   build.sh
#	Author: Shane Curcuru

echo "build.sh beginning..."

if [ "$JAVA_HOME" = "" ] ; then
    echo "You must set JAVA_HOME, sorry; also JDK 1.1x add classes.zip to CLASSPATH!"
    exit 1
fi

# Default to UNIX-style pathing
CLPATHSEP=:
# if we're on a Windows box make it ;
uname | grep WIN && CLPATHSEP=\;

# Update SERVLET to point to JAR containing javax.servlet and javax.servlet.http
#    packages; alternately include this in the classpath yourself
SERVLET=/jswdk-1.0.1/lib/servlet.jar

# Default locations for each of our jars; change as needed
BINDIR=./bin
ANT=$BINDIR/ant.jar
TOOLS_JAR=$JAVA_HOME/lib/tools.jar
XERCES=$BINDIR/xerces.jar
BSF=$BINDIR/bsf.jar
BSFENGINES=$BINDIR/bsfengines.jar
DOCGENERATOR=$BINDIR/stylebook-1.0-b3_xalan-2.jar
DOCLET=$BINDIR/xalanjdoc.jar

# Make sure this classpath scheme works. I.e., can javadoc find xalanjdoc.Standard?
TEMP_CP="$ANT${CLPATHSEP}$TOOLS_JAR${CLPATHSEP}$XERCES${CLPATHSEP}$BSF${CLPATHSEP}$BSFENGINES${CLPATHSEP}$DOCGENERATOR${CLPATHSEP}$DOCLET${CLPATHSEP}$CLASSPATH"

echo "Starting Ant with targets: $@"
echo "        ...with classpath: $TEMP_CP"

"$JAVA_HOME"/bin/java -Dant.home=. -classpath "$TEMP_CP" org.apache.tools.ant.Main $@

echo "build.sh complete!"



