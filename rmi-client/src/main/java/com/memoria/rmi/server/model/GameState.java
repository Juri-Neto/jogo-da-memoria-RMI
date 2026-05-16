package com.memoria.rmi.server.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String roomCode;
    private final List<CardState> board;
    private final String currentPlayerName;
    private final Map<String, Integer> scores;
    private final GameStatus status;
    private final String message;
    private final String sessionId;

    public GameState(
        String roomCode,
        List<CardState> board,
        String currentPlayerName,
        Map<String, Integer> scores,
        GameStatus status,
        String message,
        String sessionId
    ) {
        this.roomCode = roomCode;
        this.board = Collections.unmodifiableList(board);
        this.currentPlayerName = currentPlayerName;
        this.scores = Collections.unmodifiableMap(scores);
        this.status = status;
        this.message = message;
        this.sessionId = sessionId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public List<CardState> getBoard() {
        return board;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public GameStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSessionId() {
        return sessionId;
    }
}
