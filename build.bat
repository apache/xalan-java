@echo off
rem Prerequisites: user must set JAVA_HOME
if "%JAVA_HOME%" == "" goto error
echo.
echo Xalan-J 2.x Build
echo -------------

rem Default ANT_HOME if not set
set SAVEANTHOME=%ANT_HOME%
if "%ANT_HOME%" == "" set ANT_HOME=.

rem This automatically adds system classes to CLASSPATH
set SAVECP=%CLASSPATH%
if exist %JAVA_HOME%\lib\tools.jar set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar
if exist %JAVA_HOME%\lib\classes.zip set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\classes.zip

rem Default locations of jars we depend on 
set ANT=bin\ant.jar
set XERCES=bin\xerces.jar
set BSF=bin\bsf.jar
set BSFENGINES=bin\bsfengines.jar
set DOCGENERATOR=bin\stylebook-1.0-b2.jar
set DOCLET=bin\xalanjdoc.jar

rem DOCLET must be on system CLASSPATH for javadocs task to work. Just including it in
rem -classpath arg for java or javadoc call doesn't work....
set CLASSPATH=%ANT%;%XERCES%;%BSF%;%BSFENGINES%;%DOCGENERATOR%;%DOCLET%;%CLASSPATH%
echo.
echo Building with classpath %CLASSPATH%
echo Starting Ant...

rem Temporarily, up the -mx memory since we compile a really big glob of files
echo %JAVA_HOME%\bin\java.exe -mx64m -Dant.home="%ANT_HOME%" -classpath "%CLASSPATH%" org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
%JAVA_HOME%\bin\java.exe -mx64m -Dant.home="%ANT_HOME%" -classpath "%CLASSPATH%" org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

goto end

:error

echo "ERROR: JAVA_HOME not found in your environment."
echo.
echo "Please, set the JAVA_HOME environment variable to match the"
echo "root directory of the Java Virtual Machine you want to use."

:end
rem Cleanup environment variables
set CLASSPATH=%SAVECP%
set SAVECP=
set ANT_HOME=%SAVEANTHOME%
set SAVEANTHOME=
set XERCES=
set BSF=
set BSFENGINES=
set DOCLET=
set ANT=
