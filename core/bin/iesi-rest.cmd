@echo off
call set CURRENT_DIR="%CD%"
call set SCRIPT_DIR=%~dp0

setLocal EnableDelayedExpansion

cd !SCRIPT_DIR!

call iesi-setenv.cmd

call set LIB_DIR="%CD%\..\lib"

cd !LIB_DIR!

java "-Dloader.path=." -jar .\%IESI_REST_JAR%

cd !CURRENT_DIR!