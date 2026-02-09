@echo off
setlocal

echo =================================================
echo   HOPL - Website Compliance Scanner
echo   Building release JAR...
echo =================================================
echo.

cd /d "%~dp0"

REM Step 1: Build frontend
echo [1/4] Building React frontend...
cd frontend
call npm ci --silent 2>nul || call npm install --silent
call npm run build
echo   Frontend built successfully.
cd ..

REM Step 2: Copy frontend to Spring Boot static resources
echo [2/4] Copying frontend assets...
if not exist src\main\resources\static mkdir src\main\resources\static
del /q /s src\main\resources\static\* 2>nul
xcopy /E /Y /Q frontend\dist\* src\main\resources\static\
echo   Assets copied.

REM Step 3: Build Spring Boot JAR
echo [3/4] Building Spring Boot JAR...
call mvnw.cmd clean package -DskipTests -q
echo   JAR built successfully.

REM Step 4: Copy to release location
echo [4/4] Copying to release location...
copy /Y target\hopl-1.0.0.jar "C:\Users\cpereiro\Documents\hoplRelease.jar"

echo.
echo =================================================
echo   BUILD COMPLETE!
echo   JAR: C:\Users\cpereiro\Documents\hoplRelease.jar
echo.
echo   To run:
echo     java -jar "C:\Users\cpereiro\Documents\hoplRelease.jar"
echo.
echo   Then open: http://localhost:8080
echo =================================================

endlocal
