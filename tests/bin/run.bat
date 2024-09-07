@echo off

rem Windows batch file to run Xalan-J's XSLT 3.0 and XPath 3.1
rem test suite from command line of Xalan-J's XSLT 3.0 src build 
rem distribution.

rem Author : Mukul Gandhi <mukulg@apache.org>

rem Environment variable referring to local file system path of JRE/JDK's home directory 
SET JAVA_HOME=d:\jdk-1.8

rem Environment variable referring to the current directory
SET CD=%~dp0

SET XERCES_LIB_DIR=%CD%..\..\lib\endorsed

SET XALAN_SRC_DIST_HOME=%CD%..\..

SET JUNIT_JARS=%CD%lib\org.junit_4.13.2.v20230809-1000.jar;%CD%lib\org.hamcrest_2.2.0.jar
SET XALAN_TEST_DRIVER_JAR=%CD%lib\xalan_xsl3_tests.jar

%JAVA_HOME%\bin\java -Djava.endorsed.dirs=%XERCES_LIB_DIR%;%XALAN_SRC_DIST_HOME%\build;%XALAN_SRC_DIST_HOME%\lib -classpath %XALAN_TEST_DRIVER_JAR%;%JUNIT_JARS% -Dfile.encoding=utf-8 org.junit.runner.JUnitCore org.apache.xalan.tests.xslt3.AllXsl3Tests
