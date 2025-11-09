# Launcher script for the Flashcard Import Tool
# This script sets up the environment and starts the Streamlit app

Write-Host "🎴 Flashcard Import Tool Launcher" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# Check if we're in the correct directory
if (-not (Test-Path "app.py")) {
    Write-Host "❌ Error: app.py not found!" -ForegroundColor Red
    Write-Host "Please run this script from the import-flashcards.py directory" -ForegroundColor Yellow
    exit 1
}

# Check if streamlit is installed
$streamlitInstalled = pip list | Select-String "streamlit"
if (-not $streamlitInstalled) {
    Write-Host "⚠️  Streamlit not found. Installing dependencies..." -ForegroundColor Yellow
    pip install -r requirements.txt
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to install dependencies" -ForegroundColor Red
        exit 1
    }
}

# Check if API client is installed
$apiClientInstalled = pip list | Select-String "openapi-client"
if (-not $apiClientInstalled) {
    Write-Host "⚠️  OpenAPI client not found. Installing..." -ForegroundColor Yellow
    Push-Location
    Set-Location "..\shared\api_client"
    pip install -e .
    Pop-Location
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "⚠️  Warning: Failed to install API client" -ForegroundColor Yellow
        Write-Host "You may need to install it manually from ../shared/api_client" -ForegroundColor Yellow
    }
}

# Check for Google Cloud credentials
if (-not $env:GOOGLE_APPLICATION_CREDENTIALS) {
    Write-Host ""
    Write-Host "⚠️  Warning: GOOGLE_APPLICATION_CREDENTIALS not set" -ForegroundColor Yellow
    Write-Host "Translation features require Google Cloud credentials" -ForegroundColor Yellow
    Write-Host "Set it with: `$env:GOOGLE_APPLICATION_CREDENTIALS='path\to\credentials.json'" -ForegroundColor Yellow
    Write-Host ""
}

# Check if API server is running
try {
    Write-Host "🔍 Checking API server..." -ForegroundColor Cyan
    $response = Invoke-WebRequest -Uri "http://localhost:8080" -TimeoutSec 2 -ErrorAction Stop
    Write-Host "✅ API server is running" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Warning: Cannot connect to API server at http://localhost:8080" -ForegroundColor Yellow
    Write-Host "Make sure the API server is running before using the app" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🚀 Starting Streamlit app..." -ForegroundColor Cyan
Write-Host ""

# Start Streamlit
streamlit run app.py

# If we get here, Streamlit has exited
Write-Host ""
Write-Host "👋 Flashcard Import Tool closed" -ForegroundColor Cyan
