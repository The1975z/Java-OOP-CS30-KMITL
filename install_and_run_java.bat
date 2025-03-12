@echo off
setlocal enabledelayedexpansion

set "PROJECT_DIR=%~dp0"
set "SRC_DIR=%PROJECT_DIR%src"
set "BIN_DIR=%PROJECT_DIR%bin"

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [INFO] Java is not installed. Installing JDK...
    
    powershell -Command "& {Invoke-WebRequest -Uri 'https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.exe' -OutFile '%TEMP%\jdk-installer.exe'}"
    
    echo [INFO] Installing JDK...
    start /wait %TEMP%\jdk-installer.exe /s

    echo [INFO] Setting up Java Path...
    setx JAVA_HOME "C:\Program Files\Java\jdk-21"
    setx PATH "%PATH%;C:\Program Files\Java\jdk-21\bin"
    
    echo [INFO] Java installation completed. Please restart the terminal and try again.
    timeout /t 10
    exit /b 0
)

if exist "%SRC_DIR%\main\Main.java" (
    set "MAIN_CLASS=main.Main"
) else if exist "%SRC_DIR%\main\GameLauncher.java" (
    set "MAIN_CLASS=main.GameLauncher"
) else (
    echo [ERROR] Could not find Main.java or GameLauncher.java in %SRC_DIR%\main
    timeout /t 10
    exit /b 1
)

cd /d "%PROJECT_DIR%"

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo [INFO] Compiling Java source files...
javac -d "%BIN_DIR%" -sourcepath "%SRC_DIR%" "%SRC_DIR%\main\*.java" "%SRC_DIR%\ui\*.java" "%SRC_DIR%\utils\*.java" "%SRC_DIR%\board\*.java" "%SRC_DIR%\game\*.java" "%SRC_DIR%\pieces\*.java"
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed. Check your code for errors.
    timeout /t 10
    exit /b 1
)

echo [INFO] Running %MAIN_CLASS%...
java -cp "%BIN_DIR%" %MAIN_CLASS%

echo [INFO] Execution finished. Press any key to exit...
pause >nul
exit /b 0
