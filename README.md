# Calculadora de Trabalho Eletrico - JavaFX Retro

Aplicacao desktop em **Java 21 + JavaFX** para calcular o trabalho necessario para montar quatro cargas nos vertices de um quadrado.

## Como abrir

Clique duas vezes em:

```text
Abrir CalculadoraTrabalhoEletrico.cmd
```

Ou use o executavel portatil:

```text
dist/CalculadoraTrabalhoEletrico/CalculadoraTrabalhoEletrico.exe
```

O pacote ja inclui JavaFX e runtime no `dist`, entao nao precisa baixar dependencias para executar.

## Melhorias visuais e interativas

- sprite novo da personagem Luna em perfil, maior e preservado acima dos cards;
- fundo animado refeito com ondas continuas por seno, sem quebras no loop;
- relogio da barra superior agora usa o horario real do computador;
- efeito sonoro curto ao clicar em `Calcular`;
- botao `Limpar` limpa campos, conversoes, substituicao e resultado.

## Formula fisica usada

O arranjo tem quatro cargas:

```text
+q   -q
-q   +q
```

O trabalho necessario para montar o sistema a partir do infinito e igual a energia potencial eletrica total:

```text
W = U = soma k(qi qj) / rij
```

Pares considerados:

- 4 lados do quadrado: cargas de sinais opostos, contribuicao negativa;
- 2 diagonais: cargas de mesmo sinal, contribuicao positiva;
- diagonal: `d = a * sqrt(2)`.

Formula simplificada:

```text
W = (k * q^2 / a) * (sqrt(2) - 4)
```

Conversoes antes do calculo:

```text
qC = qPC * 1e-12
aM = aCM / 100.0
k = 8.99e9
```

Trecho central do codigo:

```java
double k = 8.99e9;
double qC = qPc * 1e-12;
double aM = aCm / 100.0;
double trabalho = (k * qC * qC / aM) * (Math.sqrt(2) - 4);
String resultado = String.format(Locale.US, "%.3e J", trabalho);
```

Exemplo:

```text
q = 2.30 pC
a = 64.0 cm
W ~= -1.921e-13 J
```

## Tratamento de entrada

O metodo de validacao:

- bloqueia campo vazio;
- aceita virgula ou ponto decimal;
- rejeita texto invalido;
- rejeita `q <= 0`;
- rejeita `a <= 0`;
- converte sempre `q` para Coulomb e `a` para metro antes do calculo.

## Desenvolvimento

Executar em modo desenvolvimento:

```powershell
.\run.ps1
```

Gerar a versao portatil para Windows:

```powershell
.\package.ps1
```

Saidas:

```text
dist/CalculadoraTrabalhoEletrico/CalculadoraTrabalhoEletrico.exe
dist/CalculadoraTrabalhoEletrico/Abrir CalculadoraTrabalhoEletrico.cmd
dist/CalculadoraTrabalhoEletrico-windows.zip
```

Gerar a versao executavel para Linux em uma maquina Linux:

```bash
bash package-linux.sh
```

Saida:

```text
dist-linux/CalculadoraTrabalhoEletrico-linux.tar.gz
```

Tambem existe uma workflow manual em `.github/workflows/build-linux-release.yml` para gerar o pacote Linux pelo GitHub Actions.

Se estiver no Windows e quiser montar um pacote Linux portatil com Java e JavaFX incluidos, use:

```powershell
.\package-linux-portable.ps1
```

Saida:

```text
out-linux-portable/CalculadoraTrabalhoEletrico-linux-portable.tar.gz
```
