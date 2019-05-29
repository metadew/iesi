@echo off
call set CURRENT_DIR="%CD%"
call set SCRIPT_DIR=%~dp0

setLocal EnableDelayedExpansion

cd !SCRIPT_DIR!

call iesi-setenv.cmd

call set LIB_DIR="%CD%\..\lib"

Set CLASSPATH=*

set CLASSPATH=!CLASSPATH!;%LIB_DIR%\*

set CLASSPATH=!CLASSPATH!

:: go to library directory to allow for reflections to work properly
cd %LIB_DIR%

java -cp !CLASSPATH! io.metadew.iesi.launch.ScriptLauncher %*

cd !CURRENT_DIR!