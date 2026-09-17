@echo off
echo === Java 21 Download Helper ===
echo.
echo Java 25 is installed but Gradle 8.11.1 doesn't support it yet.
echo We need Java 21 for this project.
echo.
echo Please download Java 21 from one of these sources:
echo.
echo 1. Oracle JDK 21:
echo    https://www.oracle.com/java/technologies/downloads/#java21
echo    Download: Windows x64 Installer
echo.
echo 2. Microsoft OpenJDK 21:
echo    https://learn.microsoft.com/en-us/java/openjdk/download
echo    Download: microsoft-jdk-21.0.12.1-windows-x64.msi
echo.
echo 3. Adoptium Temurin 21:
echo    https://adoptium.net/temurin/releases/?version=21
echo    Download: Windows x64 JDK
echo.
echo === Installation Steps ===
echo 1. Download one of the above installers
echo 2. Run the installer as Administrator
echo 3. Accept default installation path
echo 4. After installation, update run.bat with new Java path
echo 5. Run: .\run.bat
echo.
echo Or install Docker Desktop for easier setup:
echo https://www.docker.com/products/docker-desktop/
echo.
pause