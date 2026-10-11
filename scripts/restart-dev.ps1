. "$PSScriptRoot\common.ps1"

Show-Banner
Show-Action "Restarting development environment..." Yellow

$step = 1
$totalSteps = 3

Ensure-DockerRunning

docker compose restart mtg-assistant-app *> $null
Check-Step "Backend restart success" "Backend restart failed"

Wait-ForApi

Write-Host ""
Write-Host "             MTG Deck Assistant restarted $([System.Char]::ConvertFromUtf32(0x267B))$([char]0xFE0F)" -ForegroundColor Green
Write-Host "             API        : http://localhost:8080" -ForegroundColor Green
Write-Host "             PostgreSQL : localhost:5433" -ForegroundColor Green