$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$javaFxLib = Join-Path $root "lib\javafx-sdk-21.0.4\lib"
$javaFxZip = Join-Path $root "lib\openjfx-21.0.4_windows-x64_bin-sdk.zip"
$classes = Join-Path $root "out\classes"

if (-not (Test-Path $javaFxLib)) {
    New-Item -ItemType Directory -Force -Path (Join-Path $root "lib") | Out-Null

    if (-not (Test-Path $javaFxZip)) {
        Write-Host "Baixando JavaFX SDK 21.0.4..."
        curl.exe -L "https://download2.gluonhq.com/openjfx/21.0.4/openjfx-21.0.4_windows-x64_bin-sdk.zip" -o $javaFxZip
    }

    Write-Host "Extraindo JavaFX SDK..."
    Expand-Archive -LiteralPath $javaFxZip -DestinationPath (Join-Path $root "lib") -Force
}

New-Item -ItemType Directory -Force -Path $classes | Out-Null

$jars = Get-ChildItem -Path $javaFxLib -Filter *.jar | ForEach-Object { $_.FullName }
$classpath = [string]::Join(";", $jars)
$sources = Get-ChildItem -Path (Join-Path $root "src") -Recurse -Filter *.java | ForEach-Object { $_.FullName }

javac -encoding UTF-8 -cp $classpath -d $classes $sources
Copy-Item -Path (Join-Path $root "src\resources\style.css") -Destination (Join-Path $classes "style.css") -Force

Write-Host "Build concluído em $classes"
