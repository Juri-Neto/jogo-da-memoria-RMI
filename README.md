# jogo-da-memoria

Aplicacao React criada com Vite para um jogo da memoria.

## Como executar

```bash
npm install
npm run dev
```

## Build

```bash
npm run build
```

## Java Desktop RMI

O projeto agora inclui uma implementação de backend em Java RMI e um cliente desktop em Swing.

### Iniciar o servidor RMI

Abra um terminal em `rmi-server` e execute:

```bash
javac -d target/classes src/main/java/com/memoria/rmi/server/**/*.java
java -cp target/classes com.memoria.rmi.server.Main
```

### Executar o cliente desktop

Abra outro terminal em `rmi-client` e execute:

```bash
javac -d target/classes src/main/java/com/memoria/rmi/server/**/*.java src/main/java/com/memoria/rmi/client/**/*.java
java -cp target/classes com.memoria.rmi.client.RMIClientMain
```

O cliente deve se conectar ao servidor local em `localhost:1099`. Se o servidor estiver em outra máquina, informe o endereço na interface do cliente.
