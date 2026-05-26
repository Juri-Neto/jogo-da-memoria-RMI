package com.memoria.rmi.client;

import com.memoria.rmi.server.model.CardState;
import com.memoria.rmi.server.model.GameState;
import com.memoria.rmi.server.model.GameStatus;
import com.memoria.rmi.server.model.JoinResult;
import com.memoria.rmi.server.remote.GameServer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class GameClient extends JFrame {
    private static final long serialVersionUID = 1L;
    private final JLabel statusLabel = new JLabel("Conecte-se ao servidor RMI.");
    private final JLabel scoreLabel = new JLabel("Pontuação: --");
    private final JLabel roomLabel = new JLabel("Sala: --");
    private final JPanel boardPanel = new JPanel();
    private final List<JButton> cardButtons = new ArrayList<>();
    private final JTextField playerNameField = new JTextField("Jogador");
    private final JTextField serverHostField = new JTextField("localhost");
    private final JTextField roomCodeField = new JTextField();
    private GameServer server;
    private String sessionId;
    private String roomCode;
    private GameListenerImpl listener;
    private JButton restartButton;

    public GameClient() {
        super("Memória RMI - Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(720, 640));
        setupUI();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUI() {
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        topPanel.add(new JLabel("Servidor RMI:"));
        topPanel.add(serverHostField);
        topPanel.add(new JLabel("Nome do jogador:"));
        topPanel.add(playerNameField);
        topPanel.add(new JLabel("Código da sala:"));
        topPanel.add(roomCodeField);

        JPanel buttonPanel = new JPanel();
        JButton connectButton = new JButton("Conectar");
        connectButton.addActionListener(this::handleConnect);
        JButton createButton = new JButton("Criar sala");
        createButton.addActionListener(this::handleCreateRoom);
        JButton joinButton = new JButton("Entrar na sala");
        joinButton.addActionListener(this::handleJoinRoom);
        restartButton = new JButton("Reiniciar Jogo");
        restartButton.addActionListener(this::handleRestartGame);
        restartButton.setEnabled(false);
        buttonPanel.add(connectButton);
        buttonPanel.add(createButton);
        buttonPanel.add(joinButton);
        buttonPanel.add(restartButton);

        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 16f));
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.PLAIN, 14f));
        roomLabel.setFont(roomLabel.getFont().deriveFont(Font.PLAIN, 14f));
        statusPanel.add(statusLabel);
        statusPanel.add(scoreLabel);
        statusPanel.add(roomLabel);

        boardPanel.setLayout(new GridLayout(4, 4, 10, 10));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (int i = 0; i < 16; i++) {
            JButton button = new JButton("?");
            button.setFont(button.getFont().deriveFont(Font.BOLD, 28f));
            int index = i;
            button.addActionListener(e -> handleCardFlip(index));
            cardButtons.add(button);
            boardPanel.add(button);
        }

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(northPanel, BorderLayout.NORTH);
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);
    }

    private void handleConnect(ActionEvent event) {
        String host = serverHostField.getText().trim();
        if (host.isEmpty()) {
            showError("Informe o endereço do servidor.");
            return;
        }
        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            server = (GameServer) registry.lookup("GameServer");
            statusLabel.setText("Conectado ao servidor: " + host);
        } catch (Exception e) {
            showError("Falha ao conectar ao servidor: " + e.getMessage());
        }
    }

    private void handleCreateRoom(ActionEvent event) {
        ensureConnected();
        String playerName = playerNameField.getText().trim();
        if (playerName.isEmpty()) {
            showError("Informe seu nome.");
            return;
        }
        try {
            JoinResult result = server.createRoom(playerName);
            openRoom(result);
        } catch (Exception e) {
            showError("Não foi possível criar a sala: " + e.getMessage());
        }
    }

    private void handleJoinRoom(ActionEvent event) {
        ensureConnected();
        String playerName = playerNameField.getText().trim();
        String roomCodeText = roomCodeField.getText().trim();
        if (playerName.isEmpty() || roomCodeText.isEmpty()) {
            showError("Informe seu nome e o código da sala.");
            return;
        }
        try {
            JoinResult result = server.joinRoom(roomCodeText.toUpperCase(), playerName);
            openRoom(result);
        } catch (Exception e) {
            showError("Não foi possível entrar na sala: " + e.getMessage());
        }
    }

    private void openRoom(JoinResult result) {
        this.sessionId = result.getSessionId();
        this.roomCode = result.getRoomCode();
        roomLabel.setText("Sala: " + roomCode);
        statusLabel.setText("Sala criada/entrando... aguardando estado.");
        registerListener();
    }

    private void handleRestartGame(ActionEvent event) {
        if (server == null || sessionId == null) {
            showError("Conecte-se e entre em uma sala primeiro.");
            return;
        }
        try {
            server.restartGame(sessionId);
        } catch (Exception e) {
            showError("Erro ao reiniciar jogo: " + e.getMessage());
        }
    }

    private void registerListener() {
        try {
            String serverHost = serverHostField.getText().trim();
            String localIp = null;
            if (!serverHost.isEmpty()) {
                localIp = detectLocalAddressForRemote(serverHost, 1099);
            }
            if (localIp != null && !localIp.isEmpty()) {
                System.setProperty("java.rmi.server.hostname", localIp);
            }
            listener = new GameListenerImpl(this);
            server.registerListener(sessionId, listener);
            GameState state = server.getGameState(sessionId);
            updateState(state);
        } catch (Exception e) {
            showError("Erro ao registrar listener: " + e.getMessage());
        }
    }

    /**
     * Retorna o endereço IPv4 local que o SO usaria para alcançar o host remoto fornecido.
     * Não envia dados ao servidor (usa DatagramSocket conectado).
     */
    private static String detectLocalAddressForRemote(String remoteHost, int remotePort) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(new InetSocketAddress(remoteHost, remotePort));
            InetAddress local = socket.getLocalAddress();
            if (local != null && !local.isLoopbackAddress()) {
                return local.getHostAddress();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void handleCardFlip(int index) {
        if (server == null || sessionId == null) {
            showError("Conecte-se e entre em uma sala primeiro.");
            return;
        }
        try {
            server.flipCard(sessionId, index);
        } catch (Exception e) {
            showError("Erro ao virar carta: " + e.getMessage());
        }
    }

    void onRemoteStateUpdate(GameState state) {
        if (state == null) {
            return;
        }
        updateState(state);
    }

    private void updateState(GameState state) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            statusLabel.setText(state.getMessage());
            scoreLabel.setText(buildScoreText(state));
            roomLabel.setText("Sala: " + state.getRoomCode() + " | Vez: " + state.getCurrentPlayerName());
            List<CardState> board = state.getBoard();
            for (int i = 0; i < cardButtons.size() && i < board.size(); i++) {
                CardState card = board.get(i);
                JButton button = cardButtons.get(i);
                if (card.isFaceUp() || card.isMatched()) {
                    button.setText(card.getSymbol());
                    button.setEnabled(!card.isMatched());
                    button.setBackground(card.isMatched() ? Color.LIGHT_GRAY : Color.WHITE);
                } else {
                    button.setText("?");
                    button.setEnabled(true);
                    button.setBackground(Color.ORANGE);
                }
            }
            restartButton.setEnabled(state.getStatus() == GameStatus.FINISHED);
        });
    }

    private String buildScoreText(GameState state) {
        StringBuilder builder = new StringBuilder();
        state.getScores().forEach((name, score) -> builder.append(name).append(": ").append(score).append("  "));
        return builder.toString().trim();
    }

    private void ensureConnected() {
        if (server == null) {
            throw new IllegalStateException("Cliente não conectado.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
