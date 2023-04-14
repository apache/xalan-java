#!/bin/sh
#
#=========================================================================
# Copyright 2001-2023 The Apache Software Foundation.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#=========================================================================
#
#	Name:   build.sh
#	Author: Joe Kesselman
#		Fresh port from Mukul Gandhi's revised build.bat.
#		WARNING: This currently does not include the hooks needed
#		to make the script compatable with cygwin (unix/Linux shell
#		and commands ported to run under Windows). See 
#		deprecated_build.sh to see how we handled the cygwin
#		syntax differences back in 2001. These days, Windows users
#		are more likely to use WSL, which simplifies matters.

#	See:	build.xml

#	Setup:
#          1) You must set JAVA_HOME, for example,
#	      $ export JAVA_HOME=/etc/alternatives/java_sdk

#          2) You can set ANT_HOME if you use your own Ant install, for example,
#	      $ export ANT_HOME=/usr/share/ant

echo
echo Xalan-J test automation build
echo -----------------------------

if [ "$1" = "-h" ]; then 
    echo build.sh - executes Xalan Java-based test automation
    echo   Usage:   build [target] [-D options]
    echo   Example: build api -DtestClass=TransformerAPITest -Dqetest.loggingLevel=30
    echo
    echo You MUST export the JAVA_HOME environment variable to point to the JDK
    echo You CAN export ANT_HOME environment variable if you use your own Ant install

    exit 1
fi

if [ "$JAVA_HOME" = "" ]; then 
    echo Warning: JAVA_HOME environment variable is not exported
    echo You may have meant to set it to /etc/alternatives/java_sdk
    exit 1
fi

if [ -f "$JAVA_HOME/lib/tools.jar" ]; then
    CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar
fi

JAVACMD=$JAVA_HOME/bin/java

CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar

# Since Linux has scoped environments, we don't need explicit temporary vars.
# Default is to use a copy of ant bundled with xalan-java.
if [ "$ANT_HOME" = "" ]; then 
    ANT_HOME=.
fi

# Check user's ANT_HOME to make sure it actually has what we need
if [ -f "$ANT_HOME/tools/ant.jar" ]; then
    ANT_JARS=$ANT_HOME/tools/ant.jar
else
    ANT_JARS=$ANT_HOME/lib/ant.jar:$ANT_HOME/lib/ant-launcher.jar
fi

CLASSPATH=$CLASSPATH:$ANT_JARS

XERCES_ENDORSED_DIR_PATH=lib/endorsed

XALAN_BUILD_DIR_PATH=../xalan-java/build:../build

# Reminder: Note $* versus $@ distinction
echo Running:$JAVACMD  -mx1024m -Djava.endorsed.dirs=$XERCES_ENDORSED_DIR_PATH -classpath "$CLASSPATH" org.apache.tools.ant.Main "$@"
$JAVACMD  -mx1024m -Djava.endorsed.dirs=$XERCES_ENDORSED_DIR_PATH -classpath "$CLASSPATH" org.apache.tools.ant.Main "$@"

echo "build.sh complete!"
