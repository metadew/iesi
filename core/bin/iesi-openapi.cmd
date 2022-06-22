@echo off
call set CURRENT_DIR="%CD%"
setLocal EnableDelayedExpansion

call set LIB_DIR="%CD%\..\lib"
cd !LIB_DIR!

java -cp %LIB_DIR%\* io.metadew.iesi.openapi.OpenAPILauncher %*

pause
cd !CURRENT_DIR!