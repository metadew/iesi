@echo off
call set CURRENT_DIR="%CD%"
setLocal EnableDelayedExpansion

call set LIB_DIR="%CD%\..\lib"
cd !LIB_DIR!

java -Dspring.main.web-application-type=NONE -Dlog4j.configurationFile=log4j2.xml -jar iesi-rest.jar -launch %*

cd !CURRENT_DIR!
