# Documentacao do Problema

## Descricao

O problema consiste em calcular o trabalho necessario para montar um sistema com quatro cargas eletricas posicionadas nos vertices de um quadrado.

A configuracao usada e:

```text
+q ------- -q
 |           |
 |           |
 |           |
-q ------- +q
```

As cargas positivas ficam em vertices opostos, e as cargas negativas tambem ficam em vertices opostos.

## Conceito fisico

O trabalho externo necessario para montar um conjunto de cargas eletricas e igual a energia potencial eletrica total do sistema:

```text
W = U
```

Para um par de cargas puntiformes, a energia potencial eletrica e:

```text
U = k q1 q2 / r
```

Onde:

- `k` e a constante eletrostatica;
- `q1` e `q2` sao as cargas;
- `r` e a distancia entre elas.

## Pares de cargas no quadrado

Existem seis interacoes entre quatro cargas:

- quatro interacoes nos lados do quadrado;
- duas interacoes nas diagonais.

Nos lados, as cargas vizinhas tem sinais opostos:

```text
U_lados = 4(-kq²/a)
```

Nas diagonais, as cargas tem o mesmo sinal:

```text
U_diagonais = 2(kq²/(a√2))
```

Somando:

```text
U = 4(-kq²/a) + 2(kq²/(a√2))
```

Simplificando:

```text
U = (kq²/a)(√2 - 4)
```

Como `√2 - 4` e negativo, o resultado final tambem e negativo para essa configuracao.

## Unidades

O usuario informa:

- carga em picoCoulombs (`pC`);
- distancia em centimetros (`cm`).

Antes do calculo, o programa converte para o Sistema Internacional:

```text
q(C) = q(pC) × 10^-12
a(m) = a(cm) × 10^-2
```

## Exemplo numerico

Entrada:

```text
q = 2,30 pC
a = 64 cm
```

Conversao:

```text
q = 2,30 × 10^-12 C
a = 0,64 m
```

Substituicao:

```text
W = ((8,99 × 10^9)(2,30 × 10^-12)² / 0,64)(√2 - 4)
```

Resultado:

```text
W ≈ -1,92 × 10^-13 J
```
