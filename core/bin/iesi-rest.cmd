@echo off
call set CURRENT_DIR="%CD%"
call set SCRIPT_DIR=%~dp0

setLocal EnableDelayedExpansion

cd !SCRIPT_DIR!

call iesi-setenv.cmd

call set LIB_DIR="%CD%\..\rest"

cd %LIB_DIR%

java -Dlog4j.configurationFile=log4j2.xml -jar iesi-rest-0.1.0.jar %*

cd !CURRENT_DIR!