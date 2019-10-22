@echo off
call set CURRENT_DIR=%CD%
call set SCRIPT_DIR=%~dp0

CD %SCRIPT_DIR%
CD ..

call set IESI_HOME=%CD%
call set "IESI_HOME=%%IESI_HOME:\=/%%"
call set IESI_REST_JAR=iesi-rest-0.0.1-SNAPSHOT.thin.jar


:: bin
echo.iesi.home=%IESI_HOME%>"%IESI_HOME%"\bin\iesi-home.conf

:: sbin
echo.iesi.home=%IESI_HOME%>"%IESI_HOME%"\sbin\iesi-home.conf

:: lib
echo.iesi.home=%IESI_HOME%>"%IESI_HOME%"\lib\iesi-home.conf
:: lib
echo.iesi.home=%IESI_HOME%>"%IESI_HOME%"\rest\iesi-home.conf

echo F | xcopy "%IESI_HOME%"\conf\iesi-log4j2-cli.xml "%IESI_HOME%"\lib\log4j2.xml /y /q > nul 2>&1
echo F | xcopy "%IESI_HOME%"\conf\iesi-log4j2-cli.xml "%IESI_HOME%"\rest\log4j2.xml /y  /q > nul 2>&1

CD %CURRENT_DIR%
EXIT /B %ERRORLEVEL%
