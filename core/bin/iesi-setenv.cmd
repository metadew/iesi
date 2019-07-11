@echo off
call set CURRENT_DIR=%CD%
call set SCRIPT_DIR=%~dp0

CD %SCRIPT_DIR%
CD ..

call set IESI_HOME=%CD%
call set "IESI_HOME=%%IESI_HOME:\=/%%"

:: bin
echo.iesi.home=%IESI_HOME%>"%IESI_HOME%"\bin\iesi-home.conf

:: sbin
echo.iesi.home=%IESI_HOME%>"%IESI_HOME%"\sbin\iesi-home.conf

:: lib
echo.iesi.home=%IESI_HOME%>"%IESI_HOME%"\lib\iesi-home.conf

CD %CURRENT_DIR%
EXIT /B %ERRORLEVEL%
