. "$PSScriptRoot\common.ps1"

if (-not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    Write-Host "JAVA_HOME is invalid" -ForegroundColor Red
    exit 1
}

$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Show-Banner
Show-Action "Starting development environment..." Cyan

$step = 1
$totalSteps = 7

Ensure-DockerRunning

.\mvnw.cmd -q clean
Check-Step "Maven clean success" "Maven clean failed"

.\mvnw.cmd -q package
Check-Step "Maven package success" "Maven package failed"

docker build -t mtg-deck-assistant . *> $null
Check-Step "Docker build success" "Docker build failed"

docker compose up -d --force-recreate *> $null
Check-Step "Docker compose up success" "Docker compose up failed"

Wait-ForServicesHealthy

Wait-ForApi

Write-Host ""
Write-Host "             MTG Deck Assistant is up and running $([System.Char]::ConvertFromUtf32(0x1F389))  $([System.Char]::ConvertFromUtf32(0x1F525))" -ForegroundColor Green
Write-Host "             API        : http://localhost:8080" -ForegroundColor Green
Write-Host "             PostgreSQL : localhost:5433" -ForegroundColor Green