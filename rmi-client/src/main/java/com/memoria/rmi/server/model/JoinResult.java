package com.memoria.rmi.server.model;

import java.io.Serializable;

public class JoinResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String sessionId;
    private final String roomCode;

    public JoinResult(String sessionId, String roomCode) {
        this.sessionId = sessionId;
        this.roomCode = roomCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRoomCode() {
        return roomCode;
    }
}
