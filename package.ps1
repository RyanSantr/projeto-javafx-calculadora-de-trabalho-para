$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$classes = Join-Path $root "out\classes"
$packageInput = Join-Path $root "out\package"
$jarPath = Join-Path $packageInput "calculadora-cargas-eletricas.jar"
$dist = Join-Path $root "dist"
$javaFxLib = Join-Path $root "lib\javafx-sdk-21.0.4\lib"

& (Join-Path $root "build.ps1")

if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    throw "jpackage nao encontrado. Verifique se o JDK 21 esta instalado e no PATH."
}

if (-not (Get-Command jar -ErrorAction SilentlyContinue)) {
    throw "jar nao encontrado. Verifique se o JDK 21 esta instalado e no PATH."
}

New-Item -ItemType Directory -Force -Path $packageInput | Out-Null
if (Test-Path $jarPath) {
    Remove-Item -LiteralPath $jarPath -Force
}

jar --create --file $jarPath --main-class Main -C $classes .

if (Test-Path $dist) {
    Remove-Item -LiteralPath $dist -Recurse -Force
}

jpackage `
    --type app-image `
    --name CalculadoraCargas `
    --app-version 1.0.0 `
    --vendor "Projeto Universitario" `
    --input $packageInput `
    --main-jar "calculadora-cargas-eletricas.jar" `
    --main-class Main `
    --module-path $javaFxLib `
    --add-modules javafx.controls `
    --dest $dist

$appFolder = Join-Path $dist "CalculadoraCargas"
$zipPath = Join-Path $dist "CalculadoraCargas-windows.zip"

if (Test-Path $zipPath) {
    Remove-Item -LiteralPath $zipPath -Force
}

Compress-Archive -Path $appFolder -DestinationPath $zipPath

Write-Host "Executavel criado em: $appFolder\CalculadoraCargas.exe"
Write-Host "Pacote ZIP criado em: $zipPath"
