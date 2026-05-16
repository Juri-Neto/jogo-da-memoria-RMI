package com.memoria.rmi.server;

import com.memoria.rmi.server.impl.GameServerImpl;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        int port = 1099;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Porta inválida. Usando 1099.");
            }
        }

        try {
            Registry registry = LocateRegistry.createRegistry(port);
            GameServerImpl server = new GameServerImpl();
            registry.rebind("GameServer", server);
            System.out.printf("RMI GameServer iniciado na porta %d.%n", port);
            System.out.println("Aguardando clientes Java Swing...");
        } catch (Exception e) {
            System.err.println("Falha ao iniciar o servidor RMI:");
            e.printStackTrace();
        }
    }
}
