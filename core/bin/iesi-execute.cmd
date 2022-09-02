@echo off
call set CURRENT_DIR="%CD%"
setLocal EnableDelayedExpansion

call set LIB_DIR="%CD%\..\lib"
cd !LIB_DIR!

java -Dlogging.config=./log4j2-disabled -jar iesi-core.jar -launcher execution %*

cd !CURRENT_DIR!