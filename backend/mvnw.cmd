@echo off
@REM Maven Wrapper script for Windows

setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_PROPERTIES="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties"

@REM Download wrapper jar if it doesn't exist
if not exist %WRAPPER_JAR% (
    echo Downloading Maven Wrapper...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile %WRAPPER_JAR%"
)

@REM Read distributionUrl from properties file
for /f "tokens=2 delims==" %%a in ('findstr "distributionUrl" %WRAPPER_PROPERTIES%') do set DIST_URL=%%a

@REM Execute Maven
%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar %*

endlocal
