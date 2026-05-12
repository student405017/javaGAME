@echo off
setlocal

set "JAVA_BIN="

where javac >nul 2>nul
if errorlevel 1 (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javac.exe" set "JAVA_BIN=%JAVA_HOME%\bin\"
    )
    if not defined JAVA_BIN (
        for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-*-hotspot") do (
            if exist "%%~fD\bin\javac.exe" set "JAVA_BIN=%%~fD\bin\"
        )
    )
    if not defined JAVA_BIN (
        echo javac was not found. Please install Java 17 JDK or newer and add it to PATH.
        exit /b 1
    )
)

where java >nul 2>nul
if errorlevel 1 (
    if not defined JAVA_BIN (
        if defined JAVA_HOME (
            if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_BIN=%JAVA_HOME%\bin\"
        )
    )
    if not defined JAVA_BIN (
        echo java was not found. Please install Java 17 JDK or newer and add it to PATH.
        exit /b 1
    )
)

if not exist out mkdir out
dir /s /b src\main\java\*.java > sources.txt
"%JAVA_BIN%javac" -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
    echo Compile failed.
    exit /b 1
)

if /i "%~1"=="compile" (
    echo Compile succeeded.
    exit /b 0
)

"%JAVA_BIN%java" -cp out aishooter.GameFrame
