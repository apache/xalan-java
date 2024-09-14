@echo off
rem
rem ==========================================================================
rem = Licensed to the Apache Software Foundation (ASF) under one or more
rem = contributor license agreements.  See the NOTICE file distributed with
rem = this work for additional information regarding copyright ownership.
rem = The ASF licenses this file to You under the Apache License, Version 2.0
rem = (the "License"); you may not use this file except in compliance with
rem = the License.  You may obtain a copy of the License at
rem =
rem =     http://www.apache.org/licenses/LICENSE-2.0
rem =
rem = Unless required by applicable law or agreed to in writing, software
rem = distributed under the License is distributed on an "AS IS" BASIS,
rem = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem = See the License for the specific language governing permissions and
rem = limitations under the License.
rem ==========================================================================
rem
rem     build.bat: Build Xalan-J 3.x using Ant
rem     Author: Xalan-J team
rem 
rem     Usage: build [ant-options] [targets]
rem
rem     Setup:
rem        1) You must set JAVA_HOME
rem
rem        2) You can set ANT_HOME if you use your own Ant install

echo.
echo Xalan-J 3.x Build
echo -----------------

if "%JAVA_HOME%"=="" goto noJavaHome

if exist "%JAVA_HOME%\lib\tools.jar" (
   set _CLASSPATH=%JAVA_HOME%\lib\tools.jar
)

set _JAVACMD=%JAVA_HOME%\bin\java

rem Default ANT_HOME to the one what user has set
if not "%ANT_HOME%"=="" set _ANT_HOME=%ANT_HOME%
if "%ANT_HOME%"=="" set _ANT_HOME=.

if exist "%_ANT_HOME%\tools\ant.jar" (
   set _ANT_JARS=%_ANT_HOME%\tools\ant.jar
) else (
   set _ANT_JARS=%_ANT_HOME%\lib\ant.jar;%_ANT_HOME%\lib\ant-launcher.jar
)

set _CLASSPATH=%_CLASSPATH%;%_ANT_JARS%

set XERCES_ENDORSED_DIR_PATH=lib\endorsed

@echo on
"%_JAVACMD%" -mx1024m -Djava.endorsed.dirs=%XERCES_ENDORSED_DIR_PATH% -classpath "%_CLASSPATH%" org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
@echo off

goto end

:noJavaHome
echo Warning: JAVA_HOME environment variable is not set

:end
rem Cleanup environment variables
set _JAVACMD=
set _CLASSPATH=
set _ANT_HOME=
set _ANT_JARS=
