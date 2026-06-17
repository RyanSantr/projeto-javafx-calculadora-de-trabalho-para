# Guia de Execucao

## Requisitos

- Java JDK 21 instalado;
- PowerShell no Windows;
- conexao com a internet na primeira execucao, caso o JavaFX SDK ainda nao esteja em `lib/`.

## Execucao recomendada

Na raiz do projeto:

```powershell
.\run.ps1
```

Esse comando compila e abre a aplicacao.

## Execucao pelo IntelliJ IDEA

1. Abra a pasta do projeto no IntelliJ.
2. Configure o Project SDK como Java 21.
3. Execute `.\build.ps1` uma vez para baixar o JavaFX SDK, se necessario.
4. Abra a classe `Main`.
5. Crie uma configuracao de execucao com:

```text
Main class: Main
VM options: --module-path lib/javafx-sdk-21.0.4/lib --add-modules javafx.controls
```

## Execucao pelo Maven

Se o Maven estiver instalado:

```bash
mvn javafx:run
```

O `pom.xml` declara a dependencia `javafx-controls`.

## Problemas comuns

### Java nao encontrado

Verifique:

```powershell
java -version
javac -version
```

Ambos devem indicar Java 21.

### JavaFX nao encontrado

Execute:

```powershell
.\build.ps1
```

O script baixa e extrai o SDK automaticamente.

### PowerShell bloqueou o script

Execute:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1
```
