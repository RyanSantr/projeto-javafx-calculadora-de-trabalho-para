# Arquitetura do Projeto

## Visao geral

O projeto segue uma separacao simples entre:

- inicializacao da aplicacao;
- camada de interface;
- camada de calculo;
- recursos visuais.

Essa organizacao facilita a leitura do codigo e evita misturar calculos fisicos diretamente com componentes JavaFX.

## Estrutura

```text
src/
├── Main.java
├── model/
│   └── PhysicsCalculator.java
├── view/
│   ├── MainView.java
│   ├── ChargeSquarePane.java
│   ├── ChargeSquare3DPane.java
│   └── WorkGraphPane.java
└── resources/
    └── style.css
```

## Main.java

Classe de entrada da aplicacao.

Responsabilidades:

- estender `Application`;
- criar a `Scene`;
- aplicar o arquivo `style.css`;
- configurar titulo e tamanho minimo da janela;
- exibir o `Stage`.

## MainView.java

Classe principal da interface grafica.

Responsabilidades:

- criar o painel esquerdo de entradas;
- criar o painel central com abas de visualizacao;
- criar o painel direito com os detalhes do calculo;
- validar os campos digitados;
- converter texto para `double`;
- chamar `PhysicsCalculator`;
- formatar o resultado em notacao cientifica.
- atualizar resultado e grafico automaticamente enquanto o usuario digita.

## PhysicsCalculator.java

Classe responsavel pela regra de calculo.

Metodos:

```java
double picoToCoulomb(double q)
double cmToMeter(double a)
WorkResult calculateFromUserUnits(double chargePc, double sideCm)
double calculateWork(double q, double a)
```

O metodo `calculateFromUserUnits` recebe os valores digitados pelo usuario e devolve um `WorkResult` com:

- carga original em `pC`;
- lado original em `cm`;
- carga convertida para Coulomb;
- lado convertido para metro;
- trabalho final em Joules.

O metodo `calculateWork` espera valores ja convertidos para:

- Coulomb (`C`);
- metro (`m`).

## ChargeSquarePane.java

Classe responsavel apenas pelo desenho do sistema de cargas.

Componentes usados:

- `Line` para os lados do quadrado;
- `Circle` para representar as cargas;
- `Text` para os simbolos `+q`, `-q` e o lado `a`.

## ChargeSquare3DPane.java

Classe responsavel pela visualizacao 3D interativa.

Componentes usados:

- `SubScene` para renderizar conteudo 3D;
- `Sphere` para representar cargas;
- `Cylinder` para representar os lados do quadrado;
- `PerspectiveCamera` para dar profundidade;
- `RotateTransition` e `AnimationTimer` para animacao;
- eventos de mouse para rotacao manual e zoom;
- particulas para indicar o fluxo entre cargas opostas.

## WorkGraphPane.java

Classe responsavel pelo grafico `W x q`.

O grafico usa `LineChart`, `NumberAxis` e uma serie de pontos calculados com a mesma classe `PhysicsCalculator`.

## style.css

Arquivo CSS usado para manter a interface moderna e separar estilo de logica.

Define:

- cores dos paineis;
- botoes;
- campos de texto;
- mensagens de erro;
- estilo do quadrado;
- estilo das cargas.

## Scripts

| Arquivo | Funcao |
| --- | --- |
| `build.ps1` | Baixa JavaFX se necessario e compila o projeto. |
| `run.ps1` | Compila e executa a aplicacao. |
| `package.ps1` | Gera uma pasta executavel com `jpackage` e cria um ZIP para entrega. |
| `package-linux.sh` | Gera uma pasta executavel Linux e cria um `.tar.gz` para entrega. |
