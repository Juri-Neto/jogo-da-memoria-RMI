package com.memoria.rmi.server.impl;

import com.memoria.rmi.server.model.CardState;
import com.memoria.rmi.server.model.GameState;
import com.memoria.rmi.server.model.GameStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class GameRoom {
    private final String roomCode;
    private final List<Card> cards;
    private final Map<String, Player> players = new HashMap<>();
    private String currentPlayerId;
    private GameStatus status = GameStatus.WAITING;
    private int firstSelection = -1;
    private boolean pendingMismatch = false;
    private int pendingMismatchFirstIndex = -1;
    private int pendingMismatchSecondIndex = -1;
    private String message = "Aguardando jogador...";

    GameRoom(String roomCode) {
        this.roomCode = roomCode;
        this.cards = buildShuffledDeck();
    }

    synchronized String addPlayer(String playerName) {
        if (players.size() >= 2) {
            throw new IllegalStateException("Sala cheia.");
        }
        String sessionId = UUID.randomUUID().toString();
        Player player = new Player(sessionId, playerName);
        players.put(sessionId, player);
        if (players.size() == 1) {
            currentPlayerId = sessionId;
            message = "Aguardando um segundo jogador...";
        }
        if (players.size() == 2) {
            status = GameStatus.PLAYING;
            message = "O jogo começou! " + players.get(currentPlayerId).getName() + " começa.";
        }
        return sessionId;
    }

    synchronized void removePlayer(String sessionId) {
        players.remove(sessionId);
        if (players.isEmpty()) {
            return;
        }
        status = GameStatus.WAITING;
        firstSelection = -1;
        message = "Um jogador saiu. Aguardando novo jogador...";
    }

    synchronized boolean flipCard(String sessionId, int index) {
        if (status != GameStatus.PLAYING) {
            throw new IllegalStateException("O jogo ainda não começou.");
        }
        if (!sessionId.equals(currentPlayerId)) {
            throw new IllegalStateException("Não é sua vez.");
        }
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException("Índice de carta inválido.");
        }
        if (pendingMismatch) {
            hidePendingMismatch();
        }
        Card card = cards.get(index);
        if (card.isMatched() || card.isFaceUp()) {
            throw new IllegalStateException("Carta inválida.");
        }
        card.setFaceUp(true);
        if (firstSelection == -1) {
            firstSelection = index;
            message = players.get(sessionId).getName() + " revelou uma carta.";
            return false;
        }

        Card firstCard = cards.get(firstSelection);
        if (firstCard.getSymbol().equals(card.getSymbol())) {
            firstCard.setMatched(true);
            card.setMatched(true);
            players.get(sessionId).increaseScore();
            message = players.get(sessionId).getName() + " encontrou um par!";
            firstSelection = -1;

            if (allCardsMatched()) {

    status = GameStatus.FINISHED;

    int highestScore = -1;
    List<Player> winners = new ArrayList<>();

    for (Player player : players.values()) {

        if (player.getScore() > highestScore) {

            highestScore = player.getScore();

            winners.clear();
            winners.add(player);

        } else if (player.getScore() == highestScore) {

            winners.add(player);
        }
    }

    if (winners.size() == 1) {

        message = "Jogo finalizado! "
                + winners.get(0).getName()
                + " venceu.";

    } else {

        message = "Jogo finalizado! Empate entre ";

        for (int i = 0; i < winners.size(); i++) {

            message += winners.get(i).getName();

            if (i < winners.size() - 1) {
                message += " e ";
            }
        }

        message += ".";
    }
}

return false;

        } else {
            pendingMismatch = true;
            pendingMismatchFirstIndex = firstSelection;
            pendingMismatchSecondIndex = index;
            firstSelection = -1;
            currentPlayerId = getOtherPlayerId(sessionId);
            message = "Não foi par. Vez de " + players.get(currentPlayerId).getName() + ".";
            return true;
        }
    }

    synchronized GameState buildState(String sessionId) {
        List<CardState> board = new ArrayList<>(cards.size());
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            String symbol = card.isFaceUp() || card.isMatched() ? card.getSymbol() : "?";
            board.add(new CardState(i, symbol, card.isFaceUp(), card.isMatched()));
        }
        String currentPlayerName = currentPlayerId == null ? "Aguardando" : players.get(currentPlayerId).getName();
        Map<String, Integer> scores = new HashMap<>();
        players.values().forEach(player -> scores.put(player.getName(), player.getScore()));
        return new GameState(roomCode, board, currentPlayerName, scores, status, message, sessionId);
    }

    synchronized int getPlayerCount() {
        return players.size();
    }

    synchronized GameStatus getStatus() {
        return status;
    }

    String getRoomCode() {
        return roomCode;
    }

    synchronized List<String> getSessionIds() {
        return new ArrayList<>(players.keySet());
    }

    synchronized boolean hidePendingMismatchIfActive() {
        if (!pendingMismatch) {
            return false;
        }
        hidePendingMismatch();
        return true;
    }

    private void hidePendingMismatch() {
        if (pendingMismatchFirstIndex >= 0) {
            Card firstCard = cards.get(pendingMismatchFirstIndex);
            if (!firstCard.isMatched()) {
                firstCard.setFaceUp(false);
            }
        }
        if (pendingMismatchSecondIndex >= 0) {
            Card secondCard = cards.get(pendingMismatchSecondIndex);
            if (!secondCard.isMatched()) {
                secondCard.setFaceUp(false);
            }
        }
        pendingMismatch = false;
        pendingMismatchFirstIndex = -1;
        pendingMismatchSecondIndex = -1;
    }

    private boolean allCardsMatched() {
        return cards.stream().allMatch(Card::isMatched);
    }

    private String getOtherPlayerId(String currentId) {
        for (String id : players.keySet()) {
            if (!id.equals(currentId)) {
                return id;
            }
        }
        return currentId;
    }

    synchronized void restartGame() {
        this.cards.clear();
        this.cards.addAll(buildShuffledDeck());
        this.status = GameStatus.PLAYING;
        this.firstSelection = -1;
        this.pendingMismatch = false;
        this.pendingMismatchFirstIndex = -1;
        this.pendingMismatchSecondIndex = -1;

        players.values().forEach(Player::resetScore);

        if (!players.isEmpty()) {
            currentPlayerId = players.keySet().iterator().next();
            message = "Novo jogo começou! " + players.get(currentPlayerId).getName() + " começa.";
        }
    }

    private List<Card> buildShuffledDeck() {
        String[] symbols = {"❤️", "📧", "📢", "😊", "🤖", "😂", "😍", "👌"};
        List<Card> deck = new ArrayList<>();
        for (String symbol : symbols) {
            deck.add(new Card(symbol));
            deck.add(new Card(symbol));
        }
        Collections.shuffle(deck);
        return deck;
    }

    private static class Card {
        private final String symbol;
        private boolean matched;
        private boolean faceUp;

        Card(String symbol) {
            this.symbol = symbol;
            this.matched = false;
            this.faceUp = false;
        }

        String getSymbol() {
            return symbol;
        }

        boolean isMatched() {
            return matched;
        }

        boolean isFaceUp() {
            return faceUp;
        }

        void setMatched(boolean matched) {
            this.matched = matched;
        }

        void setFaceUp(boolean faceUp) {
            this.faceUp = faceUp;
        }
    }

    private static class Player {
        private final String sessionId;
        private final String name;
        private int score = 0;

        Player(String sessionId, String name) {
            this.sessionId = sessionId;
            this.name = name;
        }

        String getName() {
            return name;
        }

        int getScore() {
            return score;
        }

        void increaseScore() {
            score += 1;
        }

        void resetScore() {
    score = 0;
}
    }
}
