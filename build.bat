@echo off

if "%JAVA_HOME%" == "" goto error

echo.
echo Xalan-J Build
echo -------------


set ANT=bin\ant.jar
set JAVAC=%JAVA_HOME%\lib\tools.jar
set XERCES=bin\xerces.jar
set BSF=bin\bsf.jar
set BSFENGINES=bin\bsfengines.jar
set DOCLET=xdocs\xalanjdoc.jar

set HOLDINGPATH=%CLASSPATH%

rem DOCLET must be on system CLASSPATH for javadocs task to work. Just including it in
rem -classpath arg for java or javadoc call doesn't work....

set CLASSPATH=%ANT%;%JAVAC%;%XERCES%;%BSF%;%BSFENGINES%;%DOCLET%;%CLASSPATH%

echo.
echo Building with classpath %CLASSPATH%

echo.
echo Starting Ant...

%JAVA_HOME%\bin\java.exe -Dant.home="%ANT_HOME%" -classpath "%CLASSPATH%" org.apache.tools.ant.Main %1 %2 %3 %4 %5

goto end

:error

echo "ERROR: JAVA_HOME not found in your environment."
echo.
echo "Please, set the JAVA_HOME environment variable to match the"
echo "root directory of the Java Virtual Machine you want to use."

:end

set CLASSPATH=%HOLDINGPATH%
set HOLDINGPATH=
set XERCES=
set BSF=
set BSFENGINES=
set DOCLET=
set ANT=
set JAVAC=