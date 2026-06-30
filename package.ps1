$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$classes = Join-Path $root "out\classes"
$packageInput = Join-Path $root "out\package"
$jarPath = Join-Path $packageInput "calculadora-trabalho-eletrico.jar"
$dist = Join-Path $root "dist"
$javaFxLib = Join-Path $root "lib\javafx-sdk-21.0.4\lib"

function Invoke-Checked {
    param(
        [string] $Command,
        [string[]] $Arguments
    )

    & $Command @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "$Command falhou com codigo $LASTEXITCODE."
    }
}

if (-not (Test-Path $javaFxLib)) {
    throw "JavaFX SDK nao encontrado em $javaFxLib. A pasta lib\javafx-sdk-21.0.4 precisa ficar dentro do projeto."
}

if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    throw "jpackage nao encontrado. Use um JDK completo para gerar a versao portatil."
}

if (-not (Get-Command jar -ErrorAction SilentlyContinue)) {
    throw "jar nao encontrado. Use um JDK completo para gerar a versao portatil."
}

& (Join-Path $root "build.ps1")

New-Item -ItemType Directory -Force -Path $packageInput | Out-Null
if (Test-Path $jarPath) {
    Remove-Item -LiteralPath $jarPath -Force
}

Invoke-Checked "jar" @("--create", "--file", $jarPath, "--main-class", "br.com.ryan.trabalhoeletrico.Main", "-C", $classes, ".")

if (Test-Path $dist) {
    Remove-Item -LiteralPath $dist -Recurse -Force
}

Invoke-Checked "jpackage" @(
    "--type", "app-image",
    "--name", "CalculadoraTrabalhoEletrico",
    "--app-version", "1.0.0",
    "--vendor", "Projeto Universitario",
    "--input", $packageInput,
    "--main-jar", "calculadora-trabalho-eletrico.jar",
    "--main-class", "br.com.ryan.trabalhoeletrico.Main",
    "--module-path", $javaFxLib,
    "--add-modules", "javafx.controls,javafx.media",
    "--java-options", "-Dprism.order=sw",
    "--dest", $dist
)

$appFolder = Join-Path $dist "CalculadoraTrabalhoEletrico"
$runtimeBin = Join-Path $appFolder "runtime\bin"
$zipPath = Join-Path $dist "CalculadoraTrabalhoEletrico-windows.zip"
$launcherPath = Join-Path $appFolder "Abrir CalculadoraTrabalhoEletrico.cmd"
$rootLauncherPath = Join-Path $root "Abrir CalculadoraTrabalhoEletrico.cmd"

Copy-Item -Path (Join-Path $root "lib\javafx-sdk-21.0.4\bin\*.dll") -Destination $runtimeBin -Force

@"
@echo off
start "" "%~dp0CalculadoraTrabalhoEletrico.exe"
"@ | Set-Content -Path $launcherPath -Encoding ASCII

@"
@echo off
set "APP=%~dp0dist\CalculadoraTrabalhoEletrico\CalculadoraTrabalhoEletrico.exe"
if exist "%APP%" (
  start "" "%APP%"
) else (
  echo App portatil ainda nao foi gerado.
  echo Rode package.ps1 uma vez e tente novamente.
  pause
)
"@ | Set-Content -Path $rootLauncherPath -Encoding ASCII

if (Test-Path $zipPath) {
    Remove-Item -LiteralPath $zipPath -Force
}

Compress-Archive -Path $appFolder -DestinationPath $zipPath

Write-Host "Executavel criado em: $appFolder\CalculadoraTrabalhoEletrico.exe"
Write-Host "Atalho de duplo clique criado em: $launcherPath"
Write-Host "Atalho raiz criado em: $rootLauncherPath"
Write-Host "Pacote ZIP criado em: $zipPath"
