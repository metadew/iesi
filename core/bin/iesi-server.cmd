@echo off
call set CURRENT_DIR="%CD%"
call set SCRIPT_DIR=%~dp0

setLocal EnableDelayedExpansion

cd !SCRIPT_DIR!

call iesi-setenv.cmd

call set LIB_DIR="%CD%\..\lib"

Set CLASSPATH=*

set CLASSPATH=!CLASSPATH!;%LIB_DIR%\*;

set CLASSPATH=!CLASSPATH!

java -Dlog4j.configurationFile=%LIB_DIR%\log4j2.xml -cp !CLASSPATH! io.metadew.iesi.launch.ServerLauncher %*

cd !CURRENT_DIR!