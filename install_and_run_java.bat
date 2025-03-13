@echo off
setlocal enabledelayedexpansion

set "PROJECT_DIR=%~dp0"
set "SRC_DIR=%PROJECT_DIR%src"
set "BIN_DIR=%PROJECT_DIR%bin"
set "RESOURCES_DIR=%PROJECT_DIR%resources"

echo [INFO] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed.
    echo [INFO] Please install Java manually and try again.
    pause
    exit /b 1
)

echo [INFO] Java is installed. Proceeding...

:: ตรวจสอบโครงสร้างโปรเจค
dir "%PROJECT_DIR%" /b
echo.
dir "%SRC_DIR%" /b
echo.

:: ค้นหา Main class ที่มี main method
echo [INFO] Searching for main class...
set "MAIN_CLASS="

for /r "%SRC_DIR%" %%f in (*.java) do (
    type "%%f" | findstr /C:"public static void main" >nul
    if !errorlevel! equ 0 (
        set "FOUND_FILE=%%f"
        set "REL_PATH=!FOUND_FILE:%PROJECT_DIR%src\=!"
        set "CLASSNAME=!REL_PATH:.java=!"
        set "CLASSNAME=!CLASSNAME:\=.!"
        echo [INFO] Found potential main class: !CLASSNAME!
        set "MAIN_CLASS=!CLASSNAME!"
        goto compile_code
    )
)

:compile_code
if not defined MAIN_CLASS (
    echo [ERROR] Could not find any main class with 'public static void main'.
    pause
    exit /b 1
)

cd /d "%PROJECT_DIR%"

:: ลบและสร้างโฟลเดอร์ bin ใหม่
if exist "%BIN_DIR%" rmdir /s /q "%BIN_DIR%"
mkdir "%BIN_DIR%"

:: รวบรวมไฟล์ Java ทั้งหมด
echo [INFO] Finding all Java files...
set "JAVA_FILES="
for /r "%SRC_DIR%" %%f in (*.java) do (
    set "JAVA_FILES=!JAVA_FILES! "%%f""
)

:: คอมไพล์
echo [INFO] Compiling Java files...
javac -d "%BIN_DIR%" %JAVA_FILES%
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed. Check your code for errors.
    pause
    exit /b 1
)

:: คัดลอกไฟล์ resources
if exist "%RESOURCES_DIR%" (
    echo [INFO] Copying resources to bin directory...
    xcopy "%RESOURCES_DIR%" "%BIN_DIR%\resources\" /E /I /Y
)

:: ทดลองรันเกมด้วยหลาย classpath
echo [INFO] Running %MAIN_CLASS%...
echo [INFO] Attempt 1: java -cp "%BIN_DIR%" %MAIN_CLASS%
java -cp "%BIN_DIR%" %MAIN_CLASS%
if %errorlevel% equ 0 goto success

echo [INFO] Attempt 2: java -cp "%BIN_DIR%;%PROJECT_DIR%" %MAIN_CLASS%
java -cp "%BIN_DIR%;%PROJECT_DIR%" %MAIN_CLASS%
if %errorlevel% equ 0 goto success

echo [INFO] Attempt 3: java -cp "%BIN_DIR%;%PROJECT_DIR%;." %MAIN_CLASS%
java -cp "%BIN_DIR%;%PROJECT_DIR%;." %MAIN_CLASS%
if %errorlevel% equ 0 goto success

echo [INFO] Attempt 4: java -cp "%BIN_DIR%;%RESOURCES_DIR%" %MAIN_CLASS%
java -cp "%BIN_DIR%;%RESOURCES_DIR%" %MAIN_CLASS%
if %errorlevel% equ 0 goto success

echo [ERROR] All attempts to run the game failed.
echo [DEBUG] Please check that:
echo         1. Java is properly installed
echo         2. The main class has a proper main method
echo         3. All required resources are in the correct location
pause
exit /b 1

:success
echo [INFO] Game ran successfully!
echo [INFO] Press any key to exit...
pause >nul
exit /b 0
