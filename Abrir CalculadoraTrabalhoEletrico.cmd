@echo off
set "APP=%~dp0dist\CalculadoraTrabalhoEletrico\CalculadoraTrabalhoEletrico.exe"
if exist "%APP%" (
  start "" "%APP%"
) else (
  echo App portatil ainda nao foi gerado.
  echo Rode package.ps1 uma vez e tente novamente.
  pause
)
