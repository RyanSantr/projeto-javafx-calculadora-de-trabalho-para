$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$javaFxLib = Join-Path $root "lib\javafx-sdk-21.0.4\lib"
$classes = Join-Path $root "out\classes"
$resources = Join-Path $root "src\main\resources"

if (-not (Test-Path $javaFxLib)) {
    throw "JavaFX SDK nao encontrado em $javaFxLib. A pasta lib\javafx-sdk-21.0.4 precisa ficar dentro do projeto."
}

if (Test-Path $classes) {
    Remove-Item -LiteralPath $classes -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $classes | Out-Null

$jars = Get-ChildItem -Path $javaFxLib -Filter *.jar | ForEach-Object { $_.FullName }
$classpath = [string]::Join(";", $jars)
$sources = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter *.java -File | ForEach-Object { $_.FullName }

javac -encoding UTF-8 -cp $classpath -d $classes $sources

if (Test-Path $resources) {
    Copy-Item -Path (Join-Path $resources "*") -Destination $classes -Recurse -Force
}

Write-Host "Build concluido em $classes"
