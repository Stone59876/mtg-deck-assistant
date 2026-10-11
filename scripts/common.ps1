$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

function Show-Banner {
    $banner = @'

             ___  ___  _____   _____        _           _                     _     _              _
             |  \/  | |_   _| |  __ \      | |         | |                   (_)   | |            | |
             | .  . |   | |   | |  \/    __| | ___  ___| | __    __ _ ___ ___ _ ___| |_ __ _ _ __ | |_
             | |\/| |   | |   | | __    / _` |/ _ \/ __| |/ /   / _` / __/ __| / __| __/ _` | '_ \| __|
             | |  | |   | |   | |_\ \  | (_| |  __/ (__|   <   | (_| \__ \__ \ \__ \ || (_| | | | | |_
             \_|  |_/   \_/    \____/   \__,_|\___|\___|_|\_\   \__,_|___/___/_|___/\__\__,_|_| |_|\__|


'@

    Write-Host $banner -ForegroundColor Magenta
}

function Show-Action {
    param (
        [string]$Message,
        [ConsoleColor]$Color = "Cyan"
    )

    Write-Host "             $Message" -ForegroundColor $Color
    Write-Host ""
}

function Check-Step {
    param (
        [string]$SuccessMessage,
        [string]$FailMessage
    )

    if ($LASTEXITCODE -eq 0) {
        Write-Host "             [$script:step/$script:totalSteps] $SuccessMessage $([char]0x2714)" -ForegroundColor Green
        $script:step++
    } else {
        Write-Host "             [$script:step/$script:totalSteps] $FailMessage $([char]0x274C)" -ForegroundColor Red
        exit 1
    }
}

function Ensure-DockerRunning {
    param (
        [int]$MaxAttempts = 20,
        [int]$SleepSeconds = 4
    )

    docker info *> $null

    if ($LASTEXITCODE -eq 0) {
        Write-Host "             [$script:step/$script:totalSteps] Docker is running $([char]0x2714)" -ForegroundColor Green
        $script:step++
        return
    }

    Write-Host "             [$script:step/$script:totalSteps] Docker is not running, starting Docker Desktop..." -ForegroundColor Yellow

    docker desktop start --detach *> $null

    $attempt = 0

    do {
        Start-Sleep $SleepSeconds
        docker info *> $null
        $attempt++

        Write-Host "             Waiting for Docker... ($attempt/$MaxAttempts)" -ForegroundColor DarkGray
    } while ($LASTEXITCODE -ne 0 -and $attempt -lt $MaxAttempts)

    Check-Step "Docker is now running" "Docker failed to start"
}

function Wait-ForApi {
    param (
        [string]$HealthUrl = "http://localhost:8080/actuator/health",
        [int]$MaxAttempts = 15,
        [int]$SleepSeconds = 2,
        [int]$RequestTimeoutSeconds = 2
    )

    $attempt = 0
    $apiReady = $false

    do {
        try {
            $response = Invoke-RestMethod -Uri $HealthUrl -TimeoutSec $RequestTimeoutSeconds

            if ($response.status -eq "UP") {
                $apiReady = $true
            }
        }
        catch {
            $apiReady = $false
        }

        if (-not $apiReady) {
            $attempt++
            Write-Host "             Waiting for Spring Boot... ($attempt/$MaxAttempts)" -ForegroundColor DarkGray
            Start-Sleep $SleepSeconds
        }

    } while (-not $apiReady -and $attempt -lt $MaxAttempts)

    if ($apiReady) {
        Write-Host "             [$script:step/$script:totalSteps] API health check UP $([char]0x2714)" -ForegroundColor Green
        $script:step++
    } else {
        Write-Host "             [$script:step/$script:totalSteps] API health check failed $([char]0x274C)" -ForegroundColor Red
        Write-Host ""
        Write-Host "             Backend logs:" -ForegroundColor Yellow
        docker compose logs --tail=50 mtg-assistant-app
        exit 1
    }
}

function Wait-ForServicesHealthy {
    param (
        [int]$MaxAttempts = 20,
        [int]$SleepSeconds = 2
    )

    $attempt = 0
    $servicesHealthy = $false

    do {
        $dbContainer = docker compose ps -q db
        $appContainer = docker compose ps -q mtg-assistant-app

        if (-not $dbContainer -or -not $appContainer) {
            Write-Host "             Docker services not found $([char]0x274C)" -ForegroundColor Red
            docker compose ps
            exit 1
        }

        $dbHealth = docker inspect -f '{{.State.Health.Status}}' $dbContainer 2>$null
        $appHealth = docker inspect -f '{{.State.Health.Status}}' $appContainer 2>$null

        if ($dbHealth -eq "healthy" -and $appHealth -eq "healthy") {
            $servicesHealthy = $true
        } else {
            $attempt++

            Write-Host "             Waiting for Docker services... ($attempt/$MaxAttempts)" -ForegroundColor DarkGray
            Write-Host "             DB: $dbHealth | Backend: $appHealth" -ForegroundColor DarkGray

            Start-Sleep $SleepSeconds
        }

    } while (-not $servicesHealthy -and $attempt -lt $MaxAttempts)

    if ($servicesHealthy) {
        Write-Host "             [$script:step/$script:totalSteps] Docker services healthy $([char]0x2714)" -ForegroundColor Green
        $script:step++
    } else {
        Write-Host "             [$script:step/$script:totalSteps] Docker services health check failed $([char]0x274C)" -ForegroundColor Red

        Write-Host ""
        Write-Host "             Docker status:" -ForegroundColor Yellow
        docker compose ps

        Write-Host ""
        Write-Host "             Backend logs:" -ForegroundColor Yellow
        docker compose logs --tail=50 mtg-assistant-app

        exit 1
    }
}