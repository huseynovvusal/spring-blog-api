# Spring Blog API Setup Script for Windows
# This script helps set up the development environment

Write-Host "=== Spring Blog API Setup Script ===" -ForegroundColor Green
Write-Host ""

# Check if running as Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "⚠️  Please run this script as Administrator" -ForegroundColor Yellow
    Write-Host "Right-click PowerShell and select 'Run as Administrator'" -ForegroundColor Yellow
    exit 1
}

# Function to check if a command exists
function Test-Command {
    param([string]$command)
    $null = Get-Command $command -ErrorAction SilentlyContinue
    return $?
}

# Check Java installation
Write-Host "🔍 Checking Java installation..." -ForegroundColor Cyan
if (Test-Command "java") {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "✅ Java found: $javaVersion" -ForegroundColor Green
} else {
    Write-Host "❌ Java not found" -ForegroundColor Red
    Write-Host "📥 Please download Java 21 from: https://www.oracle.com/java/technologies/downloads/" -ForegroundColor Yellow
    Write-Host "   Or Microsoft OpenJDK: https://learn.microsoft.com/en-us/java/openjdk/download" -ForegroundColor Yellow
}

# Check PostgreSQL installation
Write-Host ""
Write-Host "🔍 Checking PostgreSQL installation..." -ForegroundColor Cyan
if (Test-Command "psql") {
    $psqlVersion = psql --version
    Write-Host "✅ PostgreSQL found: $psqlVersion" -ForegroundColor Green
} else {
    Write-Host "❌ PostgreSQL not found" -ForegroundColor Red
    Write-Host "📥 Please download PostgreSQL from: https://www.postgresql.org/download/windows/" -ForegroundColor Yellow
}

# Check Docker installation
Write-Host ""
Write-Host "🔍 Checking Docker installation..." -ForegroundColor Cyan
if (Test-Command "docker") {
    $dockerVersion = docker --version
    Write-Host "✅ Docker found: $dockerVersion" -ForegroundColor Green
} else {
    Write-Host "❌ Docker not found" -ForegroundColor Red
    Write-Host "📥 Please download Docker Desktop from: https://www.docker.com/products/docker-desktop/" -ForegroundColor Yellow
}

# Check if PostgreSQL service is running
Write-Host ""
Write-Host "🔍 Checking PostgreSQL service..." -ForegroundColor Cyan
try {
    $service = Get-Service -Name "postgresql*" -ErrorAction SilentlyContinue
    if ($service) {
        Write-Host "✅ PostgreSQL service found: $($service.Name)" -ForegroundColor Green
        Write-Host "   Status: $($service.Status)" -ForegroundColor Cyan
        if ($service.Status -ne "Running") {
            Write-Host "⚠️  PostgreSQL service is not running. Starting it..." -ForegroundColor Yellow
            Start-Service -Name $service.Name
            Write-Host "✅ PostgreSQL service started" -ForegroundColor Green
        }
    } else {
        Write-Host "❌ PostgreSQL service not found" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Could not check PostgreSQL service" -ForegroundColor Red
}

# Create .env file from .env.sample
Write-Host ""
Write-Host "🔧 Setting up environment variables..." -ForegroundColor Cyan
$envFile = ".env"
$envSample = ".env.sample"
if (Test-Path $envSample) {
    if (-not (Test-Path $envFile)) {
        Copy-Item $envSample $envFile
        Write-Host "✅ Created .env file from .env.sample" -ForegroundColor Green
    } else {
        Write-Host "✅ .env file already exists" -ForegroundColor Green
    }
} else {
    Write-Host "❌ .env.sample file not found" -ForegroundColor Red
}

# Check if database exists
Write-Host ""
Write-Host "🔍 Checking database connection..." -ForegroundColor Cyan
try {
    $envFileContent = Get-Content .env -ErrorAction SilentlyContinue
    if ($envFileContent) {
        Write-Host "✅ .env file loaded" -ForegroundColor Green
        Write-Host "📝 Please update .env file with your database credentials" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Could not read .env file" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Setup Summary ===" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Install Java 21 if not present" -ForegroundColor White
Write-Host "2. Install PostgreSQL if not present" -ForegroundColor White
Write-Host "3. (Optional) Install Docker Desktop for easier setup" -ForegroundColor White
Write-Host "4. Update .env file with your credentials" -ForegroundColor White
Write-Host "5. Run: .\gradlew.bat bootRun" -ForegroundColor White
Write-Host ""
Write-Host "For Docker setup (recommended):" -ForegroundColor Cyan
Write-Host "1. Install Docker Desktop" -ForegroundColor White
Write-Host "2. Run: docker-compose up -d" -ForegroundColor White
Write-Host ""