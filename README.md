# Jogo da Memória Multiplayer com Java RMI

Projeto desenvolvido em Java utilizando RMI (Remote Method Invocation) para permitir partidas multiplayer do jogo da memória entre computadores diferentes.

O sistema utiliza:

- Java RMI para comunicação remota
- Java Swing para interface gráfica desktop
- Arquitetura cliente-servidor
- Salas multiplayer para 2 jogadores

---

# Funcionalidades

- Criação de salas
- Entrada em salas existentes
- Partidas multiplayer em tempo real
- Controle de turnos
- Sistema de pontuação
- Detecção de vitória e empate
- Reinício da partida na mesma sala
- Atualização automática do estado do jogo entre clientes

---

# Tecnologias Utilizadas

- Java
- Java RMI
- Java Swing

---

# Estrutura do Projeto

```text
rmi-server/
    Servidor RMI
    Lógica do jogo
    Gerenciamento das salas

rmi-client/
    Cliente desktop Swing
    Interface gráfica
    Comunicação com servidor
```

---

# Como Executar

## 1. Iniciar o servidor

Abra um terminal na pasta:

```bash
rmi-server
```

Compile:

```bash
javac -d target/classes src/main/java/com/memoria/rmi/server/**/*.java
```

Execute:

```bash
java -cp target/classes com.memoria.rmi.server.Main
```

---

## 2. Executar o cliente

Abra outro terminal na pasta:

```bash
rmi-client
```

Compile:

```bash
javac -d target/classes src/main/java/com/memoria/rmi/server/**/*.java src/main/java/com/memoria/rmi/client/**/*.java
```

Execute:

```bash
java -cp target/classes com.memoria.rmi.client.RMIClientMain
```

---

# Multiplayer em Máquinas Diferentes

Para jogar entre computadores diferentes:

- ambos devem estar na mesma rede
- o servidor deve informar seu IP local
- os clientes devem conectar usando o IP do servidor

Exemplo:

```text
192.168.0.10
```

---

# Regras do Jogo

- Cada jogador joga em turnos
- Ao acertar um par, o jogador pontua e joga novamente
- Ao errar, o turno passa para o outro jogador
- O jogador com mais pares vence
- Em caso de mesma pontuação, ocorre empate

