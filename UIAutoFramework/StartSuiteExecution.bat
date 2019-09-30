@echo off

echo JAVA_HOME environment variable path
IF "%JAVA_HOME%" == "" (
    echo NOT FOUND
    echo Enter path to JAVA_HOME e.g. C:\Program Files\Java\jdk1.8.0_31: 
    set /p JAVA_HOME=
) ELSE (
    echo %JAVA_HOME%
)

%~d0
cd %~p0

set ProjectPath=%cd%
set ConfigFilePath=%ProjectPath%\Configurations\Config.properties

echo.
echo Please find below framework config.properties located in %ConfigFilePath% %n%

echo.
echo **********************************************Configuration File Starts********************************************************
echo.

FOR /F "tokens=1* delims==" %%A IN (%ConfigFilePath%) DO (
ECHO %%A   =    %%B 
)

echo.
echo ***********************************************Configuration File Ends*********************************************************
echo.

REM Comment the below line to ask for the edit configuration prompt
goto CONT

:ASK
echo Do you want to edit the above configurations? (Y/N)
set /P INPUT=Type input: %=%
if "%input%"=="Y" goto YES
if "%input%"=="y" goto YES
if "%input%"=="N" goto NO
if "%input%"=="n" goto NO
goto ASK

:YES
notepad %ConfigFilePath%
echo Suite execution will be triggered with the updated configurations %n%
goto CONT

:NO
echo Suite execution will be triggered with the existing configurations %n%

:CONT
taskkill /F /IM java.exe
call gradlew --scan -s clean build

set classpath=%classpath%;%ProjectPath%\BrowserDrivers\
set classpath=%classpath%;%ProjectPath%\ImportLibraries\*
set classpath=%classpath%;%ProjectPath%\build\classes\java\main\

xcopy "%ProjectPath%\build\classes\java\main" "%ProjectPath%\bin\*.*" /E /Y

java driver.TestngDriver