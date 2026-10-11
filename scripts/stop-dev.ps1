. "$PSScriptRoot\common.ps1"

Show-Banner
Show-Action "Stopping development environment..." Red
$step = 1
$totalSteps = 2

docker info *> $null

if ($LASTEXITCODE -ne 0) {
    Write-Host "             Docker is not running - nothing to stop $([char]0x2714)" -ForegroundColor Green
    exit 0
}

docker compose down *> $null
Check-Step "Docker compose down success" "Docker compose down failed"

$dbExists = docker ps -a --filter "name=mtg-deck-assistant-db" --format "{{.Names}}"
$appExists = docker ps -a --filter "name=mtg-deck-assistant-app" --format "{{.Names}}"

if (-not $dbExists -and -not $appExists) {
    Write-Host "             [$script:step/$script:totalSteps] Containers stopped and removed $([char]0x2714)" -ForegroundColor Green
    $script:step++
} else {
    Write-Host "             [$script:step/$script:totalSteps] Some containers are still present $([char]0x274C)" -ForegroundColor Red
    exit 1
}

Write-Host "             PostgreSQL data preserved $([char]0x2714)" -ForegroundColor Green
Write-Host ""
Write-Host "             MTG Deck Assistant stopped $([System.Char]::ConvertFromUtf32(0x1F6D1))" -ForegroundColor DarkGray