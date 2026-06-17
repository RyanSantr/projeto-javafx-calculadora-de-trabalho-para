$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
& (Join-Path $root "build.ps1")

java --module-path (Join-Path $root "lib\javafx-sdk-21.0.4\lib") --add-modules javafx.controls -cp (Join-Path $root "out\classes") Main
