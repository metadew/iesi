@echo off
call set CURRENT_DIR="%CD%"
setLocal EnableDelayedExpansion

call set LIB_DIR="%CD%\..\lib"
cd !LIB_DIR!

java -Xmx1G -Dlogging.config=./log4j2-disabled.xml -jar iesi-core-exec.jar -launcher execution %*

cd !CURRENT_DIR!