@echo off
call set CURRENT_DIR="%CD%"

setLocal EnableDelayedExpansion

call set LIB_DIR="%CD%\..\rest"

cd %LIB_DIR%

java -Dlog4j.configurationFile=log4j2.xml -Dspring.profiles.active=single_process -jar iesi-rest.jar %*

cd !CURRENT_DIR!