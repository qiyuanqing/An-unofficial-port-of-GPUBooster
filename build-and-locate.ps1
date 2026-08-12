# Builds the mod and prints the exact path of the resulting jar.
# Usage: right-click > Run with PowerShell, or `powershell -File build-and-locate.ps1`
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "Building Rapidyne (Unofficial GPUBooster Port)..." -ForegroundColor Cyan
Write-Host "(first build takes several minutes - it downloads and decompiles Minecraft)" -ForegroundColor DarkGray

& "$PSScriptRoot\gradlew.bat" build -x test
$buildExitCode = $LASTEXITCODE

if ($buildExitCode -ne 0) {
    Write-Host ""
    Write-Host "Build FAILED (exit code $buildExitCode). Scroll up for the error, or check:" -ForegroundColor Red
    Write-Host "  $PSScriptRoot\build\reports\problems\problems-report.html"
    exit $buildExitCode
}

$jar = Get-ChildItem "$PSScriptRoot\build\libs" -Filter "*.jar" -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -notmatch "-sources\.jar$" } |
    Select-Object -First 1

if ($jar) {
    Write-Host ""
    Write-Host "Build succeeded. Mod jar:" -ForegroundColor Green
    Write-Host $jar.FullName -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "Gradle reported success, but no jar was found under build\libs." -ForegroundColor Yellow
    Write-Host "Contents of build\libs:"
    Get-ChildItem "$PSScriptRoot\build\libs" -ErrorAction SilentlyContinue
    exit 1
}
