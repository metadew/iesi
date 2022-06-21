@echo off
call set CURRENT_DIR="%CD%"
setLocal EnableDelayedExpansion

call set LIB_DIR="%CD%\..\lib"
cd !LIB_DIR!

java -cp %LIB_DIR%\* io.metadew.iesi.launch.ExecutionLauncher %*

cd !CURRENT_DIR!