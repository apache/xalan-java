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

rem Windows batch file to run Xalan-J 3.x's XSLT 3.0 and XPath 3.1
rem test suite from command line, to verify Xalan-J XSLT 3.0 src 
rem distribution's functional quality.

rem Author: Xalan-J team

rem Environment variable referring to local file system path of JRE/JDK's home directory 
SET JAVA_HOME=d:\jdk-1.8

rem Environment variable referring to the current directory
SET CD=%~dp0

SET XERCES_LIB_DIR=%CD%..\..\lib\endorsed

SET XALAN_SRC_DIST_HOME=%CD%..\..

SET JUNIT_JARS=%CD%lib\org.junit_4.13.2.v20230809-1000.jar;%CD%lib\org.hamcrest_2.2.0.jar
SET XALAN_TEST_DRIVER_JAR=%CD%lib\xalan_xsl3_tests.jar

%JAVA_HOME%\bin\java -Djava.endorsed.dirs=%XERCES_LIB_DIR%;%XALAN_SRC_DIST_HOME%\build;%XALAN_SRC_DIST_HOME%\lib -classpath %XALAN_TEST_DRIVER_JAR%;%JUNIT_JARS% -Dfile.encoding=utf-8 org.junit.runner.JUnitCore org.apache.xalan.tests.xslt3.AllXsl3Tests
