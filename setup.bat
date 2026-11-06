@echo off
echo === Spring Blog API Quick Setup ===
echo.

REM Check Java
echo Checking Java installation...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Java is installed
    java -version
) else (
    echo [ERROR] Java not found
    echo Please download Java 21 from: https://www.oracle.com/java/technologies/downloads/
)

echo.

REM Check PostgreSQL
echo Checking PostgreSQL installation...
psql --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] PostgreSQL is installed
    psql --version
) else (
    echo [ERROR] PostgreSQL not found
    echo Please download PostgreSQL from: https://www.postgresql.org/download/windows/
)

echo.

REM Check Docker
echo Checking Docker installation...
docker --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Docker is installed
    docker --version
) else (
    echo [ERROR] Docker not found
    echo Please download Docker Desktop from: https://www.docker.com/products/docker-desktop/
)

echo.
echo === Setup Summary ===
echo.
echo Please install missing components, then:
echo 1. Update .env file with your database credentials
echo 2. Run: gradlew.bat bootRun
echo.
echo For Docker setup (recommended):
echo 1. Install Docker Desktop
echo 2. Run: docker-compose up -d
echo.
pause