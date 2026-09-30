@echo off
rem Dev helper: run the TDOP backend locally.
rem Requires JDK 21+ (set JAVA_HOME if it is not already set) and Maven 3.8+.
cd /d %~dp0
mvn spring-boot:run -Dmaven.test.skip=true
