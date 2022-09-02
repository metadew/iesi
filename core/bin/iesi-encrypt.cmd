@echo off
call set CURRENT_DIR="%CD%"
setLocal EnableDelayedExpansion

call set LIB_DIR="%CD%\..\lib"
cd !LIB_DIR!

java -jar iesi-core.jar -launcher encryption %*

cd !CURRENT_DIR!