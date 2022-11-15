@echo off
call set CURRENT_DIR="%CD%"
setLocal EnableDelayedExpansion

call set LIB_DIR="%CD%\..\lib"
cd !LIB_DIR!

java -cp $classpath io.metadew.iesi.Application -launcher metadata "$@"

cd !CURRENT_DIR!