@echo off
call set CURRENT_DIR=%CD%
call set SCRIPT_DIR=%~dp0

CD %SCRIPT_DIR%
CD ..

call set IESI_HOME=%CD%
call set "IESI_HOME=%%IESI_HOME:\=/%%"

echo iesi>"%IESI_HOME%"\conf\application-iesi-home.yml
echo   home=%IESI_HOME%>>"%IESI_HOME%"\conf\application-iesi-home.yml
echo F | xcopy "%IESI_HOME%"\conf\iesi-log4j2-cli.xml "%IESI_HOME%"\lib\log4j2.xml /y /q > nul 2>&1
echo F | xcopy "%IESI_HOME%"\conf\iesi-log4j2-cli.xml "%IESI_HOME%"\rest\log4j2.xml /y  /q > nul 2>&1

CD %CURRENT_DIR%
EXIT /B %ERRORLEVEL%
