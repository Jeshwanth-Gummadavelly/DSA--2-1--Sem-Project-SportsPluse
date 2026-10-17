@echo off
echo ===============================================================================
echo   Compiling SportsPulse - Sports Performance Analytics Platform (Java)...
echo ===============================================================================

if not exist bin mkdir bin

javac -d bin src/sportspulse/*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed! Please check Java setup.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Compilation Successful! Launching SportsPulse...
echo ===============================================================================
echo.

java -cp bin sportspulse.SportsPulseApp %*

pause
