@echo off
rem     build2.bat: Build NEW XPath API packages
rem     Usage: build [ant-options] [targets]
rem     Setup:
rem         - you should set JAVA_HOME
rem         - you can set ANT_HOME if you use your own Ant install
rem         - JAVA_OPTS is added to the java command line
rem         - PARSER_JAR may be set to use alternate parser (default:bin\xercesImpl.jar)
echo.
echo NEW XPath API packages
echo -------------

if not "%JAVA_HOME%" == "" goto setant
:noJavaHome
rem Default command used to call java.exe; hopefully it's on the path here
if "%_JAVACMD%" == "" set _JAVACMD=java
echo.
echo Warning: JAVA_HOME environment variable is not set.
echo   If build fails because sun.* classes could not be found
echo   you will need to set the JAVA_HOME environment variable
echo   to the installation directory of java.
echo.

:setant
rem Default command used to call java.exe or equivalent
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java

rem Currently, you must use Xalan's checkedin copy of ant etc.
set _ANT_HOME=..

rem Default locations of jars we depend on to run Ant on our build2.xml file
rem Set our local vars to all start with _underscore
rem NOTE: *only* include Ant and parser; other .jars must be 
rem     included in the build2.xml file to allow Eclipse use
rem Except, of course, the JavaCC.zip file which must be present too
set _ANT_JAR=%ANT_JAR%
if "%_ANT_JAR%" == "" set _ANT_JAR=%_ANT_HOME%\bin\ant.jar
set _PARSER_JAR=%PARSER_JAR%
if "%_PARSER_JAR%" == "" set _PARSER_JAR=%_ANT_HOME%\bin\xercesImpl.jar
set _XML-APIS_JAR=%XML-APIS_JAR%
if "%_XML-APIS_JAR%" == "" set _XML-APIS_JAR=%_ANT_HOME%\bin\xml-apis.jar
set _XALAN_JAR=%XALAN_JAR%
if "%_XALAN_JAR%" == "" set _XALAN_JAR=%_ANT_HOME%\build\xalan.jar


rem Attempt to automatically add system classes to _CLASSPATH
rem Use _underscore prefix to not conflict with user's settings
set _CLASSPATH=%CLASSPATH%
if exist "%JAVA_HOME%\lib\tools.jar" set _CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar
if exist "%JAVA_HOME%\lib\classes.zip" set _CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\classes.zip
set _CLASSPATH=%_ANT_JAR%;%_XML-APIS_JAR%;%_PARSER_JAR%;%_XALAN_JAR%;%_CLASSPATH%

@echo on
"%_JAVACMD%" -mx64m %JAVA_OPTS% -Dant.home="%_ANT_HOME%" -classpath "%_CLASSPATH%" org.apache.tools.ant.Main -f build2.xml %1 %2 %3 %4 %5 %6 %7 %8 %9
@echo off

goto end

:end
rem Cleanup environment variables
set _JAVACMD=
set _CLASSPATH=
set _ANT_HOME=
set _ANT_JAR=
set _PARSER_JAR=
set _XALAN_JAR=
set _XML-APIS_JAR=

