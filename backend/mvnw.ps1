# PowerShell Maven Wrapper Script
$ErrorActionPreference = "Stop"

$MAVEN_PROJECTBASEDIR = $PSScriptRoot
$WRAPPER_JAR = Join-Path $MAVEN_PROJECTBASEDIR ".mvn\wrapper\maven-wrapper.jar"
$WRAPPER_PROPERTIES = Join-Path $MAVEN_PROJECTBASEDIR ".mvn\wrapper\maven-wrapper.properties"
$MAVEN_HOME = Join-Path $env:USERPROFILE ".m2\wrapper\dists\apache-maven-3.9.6"

# Download wrapper jar if needed
if (-not (Test-Path $WRAPPER_JAR)) {
    Write-Host "Downloading Maven Wrapper JAR..."
    $wrapperUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    New-Item -ItemType Directory -Force -Path (Split-Path $WRAPPER_JAR) | Out-Null
    Invoke-WebRequest -Uri $wrapperUrl -OutFile $WRAPPER_JAR
}

# Download Maven if needed
if (-not (Test-Path $MAVEN_HOME)) {
    Write-Host "Downloading Apache Maven..."
    $mavenUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip"
    $zipFile = Join-Path $env:TEMP "apache-maven-3.9.6-bin.zip"
    Invoke-WebRequest -Uri $mavenUrl -OutFile $zipFile
    
    Write-Host "Extracting Apache Maven..."
    New-Item -ItemType Directory -Force -Path $MAVEN_HOME | Out-Null
    Expand-Archive -Path $zipFile -DestinationPath (Split-Path $MAVEN_HOME) -Force
    Remove-Item $zipFile
}

# Run Maven
$MVN_CMD = Join-Path $MAVEN_HOME "apache-maven-3.9.6\bin\mvn.cmd"
if (-not (Test-Path $MVN_CMD)) {
    $MVN_CMD = Join-Path $MAVEN_HOME "bin\mvn.cmd"
}

& $MVN_CMD $args
