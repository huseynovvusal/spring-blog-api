@echo off
setlocal

REM Set JAVA_HOME to the installed Java version
set JAVA_HOME=C:\Program Files\Java\jdk-25.0.2
set PATH=%JAVA_HOME%\bin;%PATH%

echo Using Java from: %JAVA_HOME%
java -version

echo.
echo Starting Spring Boot Application...
echo.

.\gradlew.bat bootRun