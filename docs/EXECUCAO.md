# Guia de Execucao

## Requisitos

- Java JDK 21 instalado;
- PowerShell no Windows;
- Bash no Linux, se for gerar o pacote Linux;
- conexao com a internet na primeira execucao, caso o JavaFX SDK ainda nao esteja em `lib/`.

## Execucao recomendada

Na raiz do projeto:

```powershell
.\run.ps1
```

Esse comando compila e abre a aplicacao.

Depois de aberta, a aplicacao atualiza o resultado automaticamente quando os dois campos possuem valores validos.

## Gerar executavel Windows

Na raiz do projeto:

```powershell
.\package.ps1
```

O script usa `jpackage`, ferramenta incluida no JDK 21, para criar uma pasta executavel:

```text
dist/CalculadoraCargas/CalculadoraCargas.exe
```

Dentro da mesma pasta tambem existe um launcher de duplo clique:

```text
dist/CalculadoraCargas/Abrir CalculadoraCargas.cmd
```

Tambem e criado um ZIP para entrega:

```text
dist/CalculadoraCargas-windows.zip
```

Para distribuir o programa, envie o arquivo ZIP inteiro. A pessoa que receber deve extrair a pasta e abrir `Abrir CalculadoraCargas.cmd` ou `CalculadoraCargas.exe`.

Se o WiX Toolset estiver instalado no Windows, o script tambem tenta gerar um instalador `.exe` com atalho no menu iniciar e na area de trabalho.

## Gerar app Linux

Em um Linux com JDK 21:

```bash
bash package-linux.sh
```

O script baixa o JavaFX SDK para Linux, compila o projeto e cria:

```text
dist-linux/CalculadoraCargas/Abrir CalculadoraCargas.sh
dist-linux/CalculadoraCargas-linux.tar.gz
```

Para distribuir no Linux, envie o arquivo `.tar.gz`. A pessoa deve extrair a pasta e abrir `Abrir CalculadoraCargas.sh`.

O pacote Linux deve ser gerado em Linux, porque `jpackage` nao faz cross-build confiavel entre Windows e Linux.

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
