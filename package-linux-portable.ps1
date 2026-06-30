$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$javaFxWinLib = Join-Path $root "lib\javafx-sdk-21.0.4\lib"
$buildRoot = Join-Path $root "out-linux-portable"
$classes = Join-Path $buildRoot "classes"
$packageDir = Join-Path $buildRoot "CalculadoraTrabalhoEletrico-linux"
$appDir = Join-Path $packageDir "app"
$javaFxDir = Join-Path $packageDir "javafx"
$runtimeDir = Join-Path $packageDir "runtime"
$jarPath = Join-Path $appDir "calculadora-trabalho-eletrico.jar"
$tarPath = Join-Path $buildRoot "CalculadoraTrabalhoEletrico-linux-portable.tar.gz"

$javaFxVersion = "21.0.4"
$javaFxZip = Join-Path $buildRoot "openjfx-$javaFxVersion-linux-x64.zip"
$javaFxUrl = "https://download2.gluonhq.com/openjfx/$javaFxVersion/openjfx-$javaFxVersion" + "_linux-x64_bin-sdk.zip"
$jreArchive = Join-Path $buildRoot "temurin-21-linux-x64-jre.tar.gz"
$jreUrl = "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jre/hotspot/normal/eclipse?project=jdk"

if (-not (Test-Path $javaFxWinLib)) {
    throw "JavaFX SDK do Windows nao encontrado em $javaFxWinLib. Rode primeiro a preparacao do projeto no Windows."
}

if (Test-Path $buildRoot) {
    Remove-Item -LiteralPath $buildRoot -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $classes, $appDir, $javaFxDir, $runtimeDir | Out-Null

$jars = Get-ChildItem -Path $javaFxWinLib -Filter *.jar | ForEach-Object { $_.FullName }
$classpath = [string]::Join(";", $jars)
$sources = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter *.java -File | ForEach-Object { $_.FullName }

javac -encoding UTF-8 -cp $classpath -d $classes $sources
Copy-Item -Path (Join-Path $root "src\main\resources\*") -Destination $classes -Recurse -Force
jar --create --file $jarPath --main-class br.com.ryan.trabalhoeletrico.Main -C $classes .

Invoke-WebRequest -Uri $javaFxUrl -OutFile $javaFxZip
Expand-Archive -LiteralPath $javaFxZip -DestinationPath $buildRoot -Force
Copy-Item -LiteralPath (Join-Path $buildRoot "javafx-sdk-$javaFxVersion\lib") -Destination $javaFxDir -Recurse -Force

Invoke-WebRequest -Uri $jreUrl -OutFile $jreArchive
# O tar do Windows pode falhar em arquivos de licenca com caminhos longos dentro do JRE.
# A runtime executavel nao depende dessa pasta para abrir o app.
tar -xzf $jreArchive -C $buildRoot --exclude "*/legal/*"
$jreRoot = Get-ChildItem -Path $buildRoot -Directory | Where-Object { $_.Name -like "jdk-21*" -or $_.Name -like "jre-21*" } | Select-Object -First 1
if (-not $jreRoot) {
    throw "Runtime Linux nao encontrado depois da extracao."
}
Copy-Item -Path (Join-Path $jreRoot.FullName "*") -Destination $runtimeDir -Recurse -Force

$launcher = @'
#!/usr/bin/env bash
set -euo pipefail
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$APP_DIR/runtime/bin/java" -Dprism.order=sw --module-path "$APP_DIR/javafx/lib" --add-modules javafx.controls,javafx.media -cp "$APP_DIR/app/calculadora-trabalho-eletrico.jar" br.com.ryan.trabalhoeletrico.Main
'@
Set-Content -Path (Join-Path $packageDir "Abrir CalculadoraTrabalhoEletrico.sh") -Value $launcher -Encoding UTF8

$packScript = Join-Path $buildRoot "pack-linux-tar.py"
$python = @"
import os
import tarfile

root = r'''$buildRoot'''
folder = 'CalculadoraTrabalhoEletrico-linux'
target = r'''$tarPath'''

with tarfile.open(target, 'w:gz') as tar:
    base = os.path.join(root, folder)
    for current, dirs, files in os.walk(base):
        for name in files:
            full = os.path.join(current, name)
            rel = os.path.relpath(full, root).replace(os.sep, '/')
            info = tar.gettarinfo(full, rel)
            if name.endswith('.sh') or rel.endswith('/runtime/bin/java') or '/runtime/bin/' in rel:
                info.mode = 0o755
            with open(full, 'rb') as fh:
                tar.addfile(info, fh)
        for name in dirs:
            full = os.path.join(current, name)
            rel = os.path.relpath(full, root).replace(os.sep, '/')
            info = tar.gettarinfo(full, rel)
            info.mode = 0o755
            tar.addfile(info)
"@
Set-Content -Path $packScript -Value $python -Encoding UTF8
python $packScript

Write-Host "Pacote Linux portatil criado em: $tarPath"
Write-Host "No Linux, extraia e execute: ./Abrir CalculadoraTrabalhoEletrico.sh"
