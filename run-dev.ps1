param(
    [int]$FrontendPort = 5173,
    [int]$BackendPort = 8080
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendDir = Join-Path $repoRoot "webapp-backend"
$frontendDir = Join-Path $repoRoot "webapp-frontend"

if (-not (Test-Path (Join-Path $backendDir "mvnw.cmd"))) {
    throw "Maven Wrapper not found at webapp-backend/mvnw.cmd"
}

if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    throw "Node.js is required. Install Node.js 20+ and retry."
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "Java 17+ is required. Install Java and retry."
}

if (-not (Test-Path (Join-Path $frontendDir "node_modules"))) {
    Write-Host "Installing frontend dependencies..."
    Push-Location $frontendDir
    npm install
    Pop-Location
}

$backendCmd = "Set-Location '$backendDir'; .\\mvnw.cmd spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $backendCmd | Out-Null

Write-Host "Started backend in a new PowerShell window on port $BackendPort."
Write-Host "Starting frontend in this terminal on port $FrontendPort..."
Push-Location $frontendDir
npm run dev -- --host 0.0.0.0 --port $FrontendPort
Pop-Location
