@echo off
cd /d "%~dp0"
echo Compiling AirplaneTicketingSystem...
if not exist "bin" mkdir "bin"
javac -d bin AirplaneTicketingSystem.java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful! Starting application...
echo.
java -cp bin AirplaneTicketingSystem
pause
