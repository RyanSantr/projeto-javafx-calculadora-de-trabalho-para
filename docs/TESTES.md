# Testes e Validacao

## Cenario principal

Entrada:

```text
q = 2,30 pC
a = 64 cm
```

Saida esperada:

```text
W ≈ -1,92 × 10^-13 J
```

## Validacao feita

O calculo foi conferido com:

```text
q = 2,30 × 10^-12 C
a = 0,64 m
W = ((8,99 × 10^9)(2,30 × 10^-12)² / 0,64)(√2 - 4)
W ≈ -1,92 × 10^-13 J
```

## Casos de entrada tratados

- campo vazio;
- texto que nao e numero;
- numero menor ou igual a zero;
- decimal com virgula;
- decimal com ponto.

## Resultado visual

O arquivo abaixo registra a interface renderizada com os valores do exemplo:

```text
screenshots/programa-calculadora-cargas.png
```
