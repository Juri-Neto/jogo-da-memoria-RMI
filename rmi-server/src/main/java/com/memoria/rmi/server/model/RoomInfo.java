package com.memoria.rmi.server.model;

import java.io.Serializable;

public class RoomInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String roomCode;
    private final int playerCount;
    private final GameStatus status;

    public RoomInfo(String roomCode, int playerCount, GameStatus status) {
        this.roomCode = roomCode;
        this.playerCount = playerCount;
        this.status = status;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public GameStatus getStatus() {
        return status;
    }
}
