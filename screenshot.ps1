$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
& (Join-Path $root "build.ps1")

java --module-path (Join-Path $root "lib\javafx-sdk-21.0.4\lib") --add-modules javafx.controls,javafx.swing -cp (Join-Path $root "out\classes") ScreenshotGenerator

Write-Host "Screenshot salvo em screenshots\programa-calculadora-cargas.png"
