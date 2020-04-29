@echo off
call set CURRENT_DIR="%CD%"
setLocal EnableDelayedExpansion

call set LIB_DIR="%CD%\..\lib"
cd !LIB_DIR!

java -Dlog4j.configurationFile=%LIB_DIR%\log4j2.xml -cp -cp %LIB_DIR%\* io.metadew.iesi.launch.ServerLauncher %*

cd !CURRENT_DIR!