package com.memoria.rmi.client;

import javax.swing.SwingUtilities;

public class RMIClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GameClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
