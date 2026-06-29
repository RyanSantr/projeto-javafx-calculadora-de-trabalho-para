# Testes e Validacao

## Cenario principal

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

Saida esperada:

```text
W ~= -1,92 x 10^-13 J
```

## Validacao do calculo

```text
W = ((8,99 x 10^9)(2,30 x 10^-12)^2 / 0,64)(sqrt(2) - 4)
W ~= -1,92 x 10^-13 J
```

O valor aparece negativo porque a soma das interacoes atrativas e repulsivas resulta em energia potencial total menor que zero.

## Casos tratados pela interface

- campo vazio;
- texto que nao e numero;
- numero menor ou igual a zero;
- decimal com virgula;
- decimal com ponto;
- atualizacao automatica do resultado;
- limpeza dos campos;
- historico de calculos manuais;
- grafico atualizado pelo mesmo calculo fisico.

## Validacao visual

Foram atualizados os prints da interface com os valores do exemplo:

```text
screenshots/programa-calculadora-cargas.png
screenshots/programa-calculadora-cargas-3d.png
screenshots/programa-calculadora-cargas-grafico.png
```

## Validacao tecnica

Comando executado no Windows:

```powershell
.\build.ps1
```

Resultado:

```text
Build concluido em out/classes
```

## PDF explicativo

A explicacao do calculo tambem esta registrada em:

```text
docs/calculo-cargas-eletricas.pdf
```
