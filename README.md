# Electric Field Simulator

Aplicacao desktop em **Java 21 + JavaFX** para calcular o trabalho necessario para montar quatro cargas eletricas nos vertices de um quadrado.

O projeto foi feito para trabalho universitario, com calculo fisico separado da interface, visual 2D, simulacao 3D interativa, grafico e pacote executavel para Windows e Linux.

## Objetivo

O usuario informa:

- carga `q` em picoCoulombs (`pC`);
- lado do quadrado `a` em centimetros (`cm`);
- e recebe o trabalho `W` em Joules (`J`).

## Formula

O trabalho necessario para montar o sistema e igual a energia potencial eletrica total:

```text
W = U

U = 4(-kq^2/a) + 2(kq^2/(a sqrt(2)))

U = (kq^2/a)(sqrt(2) - 4)
```

Onde:

```text
k = 8,99 x 10^9 N.m^2/C^2
1 pC = 10^-12 C
1 cm = 10^-2 m
```

## Interface premium

A tela principal foi desenhada como um dashboard cientifico escuro:

- painel esquerdo com entradas, botoes e resumo do modelo fisico;
- painel central com abas para cena 2D, ondas 3D e grafico `W x q`;
- painel direito com conversoes, formula, substituicao e resultado final;
- historico inferior com os calculos feitos na sessao;
- tema futurista com cards, brilho, neon azul, vermelho para `+q` e ciano/azul para `-q`.

![Print da aplicacao](screenshots/programa-calculadora-cargas.png)

## Visualizacao 3D

A aba **Ondas 3D** usa um estilo 2.5D: parece tridimensional, mas e desenhada com formas 2D do JavaFX para ficar mais limpa e controlada. Ela inclui:

- cargas desenhadas como esferas 2D com brilho e sombra;
- grade isometrica estilo CAD;
- ondas eletricas lineares percorrendo as ligacoes;
- particulas animadas de fluxo em linha reta;
- luzes coloridas;
- perspectiva desenhada em ambiente pseudo-3D.

![Print da visualizacao 3D](screenshots/programa-calculadora-cargas-3d.png)

## Grafico

A aba de grafico mostra a variacao do trabalho em funcao da carga `q`, mantendo o valor atual de `a`.

![Print do grafico](screenshots/programa-calculadora-cargas-grafico.png)

## Exemplo de calculo

Entrada:

```text
q = 2,30 pC
a = 64 cm
```

Conversoes:

```text
q = 2,30 x 10^-12 C
a = 0,64 m
```

Resultado aproximado:

```text
W = -1,92 x 10^-13 J
```

## PDF explicativo

O projeto inclui um PDF com a explicacao passo a passo do problema:

```text
docs/calculo-cargas-eletricas.pdf
```

O texto fonte tambem esta em:

```text
docs/CALCULO_EXPLICADO.md
```

## Estrutura do projeto

```text
src/
|-- Main.java
|-- model/
|   `-- PhysicsCalculator.java
|-- view/
|   |-- MainView.java
|   |-- ChargeSquarePane.java
|   |-- ChargeSquare3DPane.java
|   |-- WorkGraphPane.java
|   |-- ResultCard.java
|   |-- HistoryPane.java
|   |-- Theme.java
|   `-- Animations.java
`-- resources/
    `-- style.css
```

## Classes principais

| Classe | Responsabilidade |
| --- | --- |
| `Main.java` | Inicializa o JavaFX, cria a janela, aplica o CSS e define tamanho minimo. |
| `MainView.java` | Monta o dashboard, valida entradas, chama o calculo e atualiza os paineis. |
| `PhysicsCalculator.java` | Faz conversoes, aplica a formula fisica e retorna o resultado completo. |
| `ChargeSquarePane.java` | Desenha a representacao 2D com `Circle`, `Line` e `Text`. |
| `ChargeSquare3DPane.java` | Renderiza a simulacao 2.5D isometrica com formas 2D, brilho e ondas lineares. |
| `WorkGraphPane.java` | Gera o grafico `W x q` com `LineChart`. |
| `ResultCard.java` | Organiza conversoes, formula, substituicao e resultado em cards. |
| `HistoryPane.java` | Mostra e limpa o historico de simulacoes. |
| `Theme.java` | Centraliza constantes de cor usadas no codigo Java. |
| `Animations.java` | Centraliza animacoes simples de entrada e hover. |

## Requisitos

- Java JDK 21
- Windows PowerShell para `build.ps1`, `run.ps1` e `package.ps1`
- Linux com Bash para `package-linux.sh`
- Opcional: IntelliJ IDEA
- Opcional: Maven

## Como executar no Windows

No PowerShell:

```powershell
.\run.ps1
```

O script compila e executa a classe `Main`. Se o JavaFX SDK 21.0.4 nao existir em `lib/`, o build tenta baixa-lo automaticamente.

## Como gerar o executavel Windows

```powershell
.\package.ps1
```

O pacote gerado fica em:

```text
dist/CalculadoraCargas/CalculadoraCargas.exe
dist/CalculadoraCargas/Abrir CalculadoraCargas.cmd
dist/CalculadoraCargas-windows.zip
```

Para entregar, use o ZIP. Depois de extrair, basta clicar em `CalculadoraCargas.exe` ou `Abrir CalculadoraCargas.cmd`.

## Como gerar o app Linux

Em um Linux com JDK 21:

```bash
bash package-linux.sh
```

O script gera:

```text
dist-linux/CalculadoraCargas/Abrir CalculadoraCargas.sh
dist-linux/CalculadoraCargas-linux.tar.gz
```

Observacao: o pacote Linux precisa ser gerado em Linux. O Windows nao cria app Linux nativo com `jpackage`.

## Como executar pelo IntelliJ IDEA

1. Abra esta pasta como projeto.
2. Configure o SDK do projeto como Java 21.
3. Se usar Maven, aguarde a importacao do `pom.xml`.
4. Execute a classe `Main`.

Se o IntelliJ nao reconhecer o JavaFX automaticamente, use:

```text
--module-path lib/javafx-sdk-21.0.4/lib --add-modules javafx.controls
```

## Validacoes implementadas

- campos obrigatorios;
- bloqueio de valores menores ou iguais a zero;
- suporte a decimal com virgula ou ponto;
- mensagens de erro amigaveis;
- resultado em notacao cientifica;
- calculo automatico enquanto o usuario digita;
- historico de calculos manuais;
- interface responsiva para janela desktop.

## Arquivos gerados

As pastas abaixo sao geradas localmente e nao devem ser versionadas:

```text
lib/
out/
target/
dist/
dist-linux/
```
