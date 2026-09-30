# Dev helper: run the TDOP backend locally.
# Requires JDK 21+ (set JAVA_HOME if it is not already set) and Maven 3.8+.
Set-Location $PSScriptRoot
mvn spring-boot:run -Dmaven.test.skip=true
