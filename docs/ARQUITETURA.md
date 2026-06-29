# Arquitetura do Projeto

## Visao geral

O projeto separa a regra fisica da interface grafica. Isso deixa o codigo mais facil de explicar, testar e manter.

Camadas principais:

- `Main.java`: inicializacao do JavaFX;
- `model/`: calculo fisico;
- `view/`: componentes visuais;
- `resources/`: CSS do tema.

## Estrutura

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

## Fluxo de execucao

```text
Usuario digita q e a
        |
        v
MainView valida entrada
        |
        v
PhysicsCalculator converte unidades e calcula W
        |
        v
MainView atualiza resultado, grafico, 2D/3D e historico
```

## Main.java

Responsavel por:

- iniciar a aplicacao JavaFX;
- criar a `Scene`;
- aplicar `style.css`;
- definir titulo, tamanho inicial e tamanho minimo;
- exibir o `Stage`.

## PhysicsCalculator.java

Responsavel pela parte fisica.

Metodos principais:

```java
double picoToCoulomb(double q)
double cmToMeter(double a)
double calculateWork(double q, double a)
WorkResult calculateFromUserUnits(double chargePc, double sideCm)
```

`calculateWork` recebe valores ja convertidos:

- carga em Coulomb;
- distancia em metro.

`calculateFromUserUnits` recebe os valores digitados pelo usuario:

- `q` em pC;
- `a` em cm;
- e retorna tambem os valores convertidos.

## MainView.java

Responsavel pelo dashboard principal:

- painel de entradas;
- botoes `Calcular`, `Limpar` e `Exemplo`;
- abas de visualizacao;
- painel de resultado;
- historico;
- validacao;
- formatacao de notacao cientifica.

Essa classe coordena os componentes, mas nao faz a fisica diretamente. O calculo fica no `PhysicsCalculator`.

## ChargeSquarePane.java

Desenha a representacao 2D:

- `Line` para os lados;
- `Circle` para cargas;
- `Text` para `+q`, `-q` e `a`.

As cargas positivas aparecem em vermelho e as negativas em azul/ciano.

## ChargeSquare3DPane.java

Renderiza a simulacao 3D usando JavaFX nativo:

- `SubScene`;
- `PerspectiveCamera`;
- `Sphere`;
- `Cylinder`;
- `AmbientLight`;
- `PointLight`;
- `RotateTransition`;
- `AnimationTimer`.

Elementos visuais:

- quatro cargas nos vertices;
- arcos de interacao;
- particulas animadas;
- grade estilo CAD;
- nucleo energetico central;
- campo de estrelas;
- controle por mouse e zoom por scroll.

## WorkGraphPane.java

Mostra o grafico `W x q`.

O grafico calcula varios pontos usando a mesma regra do `PhysicsCalculator`, o que evita duplicar formula em outro lugar do projeto.

## ResultCard.java

Organiza a explicacao do resultado em cards:

- conversoes;
- formula;
- substituicao dos valores;
- resultado final em destaque.

## HistoryPane.java

Mantem uma lista visual dos calculos feitos manualmente. Cada item mostra:

- horario;
- carga `q`;
- lado `a`;
- trabalho `W`.

## Theme.java

Centraliza cores usadas no codigo Java quando a cor precisa ser aplicada diretamente no componente.

## Animations.java

Centraliza animacoes simples:

- entrada com fade;
- entrada com deslocamento;
- efeito de hover nos botoes.

## style.css

Define o tema visual:

- fundo escuro;
- paineis estilo vidro;
- botoes com gradiente;
- campos com brilho ao focar;
- cards de resultado;
- tabs;
- grafico;
- historico.

## Scripts

| Arquivo | Funcao |
| --- | --- |
| `build.ps1` | Baixa JavaFX se necessario e compila o projeto. |
| `run.ps1` | Compila e executa a aplicacao. |
| `package.ps1` | Gera executavel Windows com `jpackage` e cria ZIP. |
| `package-linux.sh` | Gera pacote Linux e cria `.tar.gz`. |
