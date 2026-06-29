# Calculo do Trabalho para Montagem das Cargas

## 1. Situacao fisica

O sistema possui quatro cargas nos vertices de um quadrado:

```text
+q ------- -q
 |           |
 |           |
-q ------- +q
```

O objetivo e calcular o trabalho necessario para montar esse arranjo.

Como o trabalho externo para montar cargas eletricas e igual a energia potencial eletrica total, temos:

```text
W = U
```

## 2. Energia potencial entre duas cargas

Para duas cargas puntiformes:

```text
U = k q1 q2 / r
```

Onde:

- `k = 8,99 x 10^9 N.m2/C2`;
- `q1` e `q2` sao as cargas;
- `r` e a distancia entre elas.

## 3. Pares do quadrado

Com quatro cargas, existem seis pares de interacao:

- quatro pares nos lados do quadrado;
- dois pares nas diagonais.

Nos lados, as cargas tem sinais opostos. Por isso a energia e negativa:

```text
U_lados = 4(-kq^2/a)
```

Nas diagonais, as cargas tem o mesmo sinal. Por isso a energia e positiva:

```text
U_diagonais = 2(kq^2/(a raiz(2)))
```

Somando:

```text
U = 4(-kq^2/a) + 2(kq^2/(a raiz(2)))
```

Simplificando:

```text
U = (kq^2/a)(raiz(2) - 4)
```

Logo:

```text
W = (kq^2/a)(raiz(2) - 4)
```

## 4. Conversoes de unidades

O usuario informa:

- carga em picoCoulombs (`pC`);
- lado do quadrado em centimetros (`cm`).

O programa converte tudo para o Sistema Internacional:

```text
q(C) = q(pC) x 10^-12
a(m) = a(cm) x 10^-2
```

## 5. Exemplo numerico

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

Substituicao:

```text
W = ((8,99 x 10^9)(2,30 x 10^-12)^2 / 0,64)(raiz(2) - 4)
```

Resultado:

```text
W aproximadamente -1,92 x 10^-13 J
```

## 6. Interpretacao

O resultado e negativo porque as quatro interacoes atrativas dos lados dominam as duas interacoes repulsivas das diagonais.

Fisicamente, isso indica que o arranjo tem energia potencial total negativa.
