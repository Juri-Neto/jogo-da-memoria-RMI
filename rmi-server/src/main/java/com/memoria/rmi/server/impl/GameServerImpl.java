package com.memoria.rmi.server.impl;

import com.memoria.rmi.server.model.CardState;
import com.memoria.rmi.server.model.GameState;
import com.memoria.rmi.server.model.GameStatus;
import com.memoria.rmi.server.model.JoinResult;
import com.memoria.rmi.server.model.RoomInfo;
import com.memoria.rmi.server.remote.GameListener;
import com.memoria.rmi.server.remote.GameServer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameServerImpl extends UnicastRemoteObject implements GameServer {
    private static final long serialVersionUID = 1L;
    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();
    private final Map<String, GameListener> listeners = new ConcurrentHashMap<>();

    public GameServerImpl() throws RemoteException {
        super();
    }

    @Override
    public JoinResult createRoom(String playerName) throws RemoteException {
        String roomCode = generateRoomCode();
        GameRoom room = new GameRoom(roomCode);
        String sessionId = room.addPlayer(playerName);
        rooms.put(roomCode, room);
        sessionToRoom.put(sessionId, roomCode);
        broadcastRoomUpdate(room);
        return new JoinResult(sessionId, roomCode);
    }

    @Override
    public JoinResult joinRoom(String roomCode, String playerName) throws RemoteException {
        GameRoom room = rooms.get(roomCode);
        if (room == null) {
            throw new RemoteException("Sala não encontrada: " + roomCode);
        }
        if (room.getStatus() != GameStatus.WAITING) {
            throw new RemoteException("Não é possível entrar na sala agora.");
        }
        String sessionId = room.addPlayer(playerName);
        sessionToRoom.put(sessionId, roomCode);
        broadcastRoomUpdate(room);
        if (room.getStatus() == GameStatus.PLAYING) {
            broadcastRoomUpdate(room);
        }
        return new JoinResult(sessionId, roomCode);
    }

    @Override
    public void registerListener(String sessionId, GameListener listener) throws RemoteException {
        if (!sessionToRoom.containsKey(sessionId)) {
            throw new RemoteException("Sessão inválida.");
        }
        listeners.put(sessionId, listener);
        GameState state = getGameState(sessionId);
        listener.onStateUpdate(state);
    }

    @Override
    public GameState getGameState(String sessionId) throws RemoteException {
        String roomCode = sessionToRoom.get(sessionId);
        if (roomCode == null) {
            throw new RemoteException("Sessão não encontrada.");
        }
        GameRoom room = rooms.get(roomCode);
        if (room == null) {
            throw new RemoteException("Sala encerrada.");
        }
        return room.buildState(sessionId);
    }

    @Override
    public void flipCard(String sessionId, int cardIndex) throws RemoteException {
        String roomCode = sessionToRoom.get(sessionId);
        if (roomCode == null) {
            throw new RemoteException("Sessão inválida.");
        }
        GameRoom room = rooms.get(roomCode);
        if (room == null) {
            throw new RemoteException("Sala encerrada.");
        }
        room.flipCard(sessionId, cardIndex);
        broadcastRoomUpdate(room);
    }

    @Override
    public void leaveGame(String sessionId) throws RemoteException {
        String roomCode = sessionToRoom.remove(sessionId);
        if (roomCode == null) {
            return;
        }
        listeners.remove(sessionId);
        GameRoom room = rooms.get(roomCode);
        if (room == null) {
            return;
        }
        room.removePlayer(sessionId);
        if (room.getPlayerCount() == 0) {
            rooms.remove(roomCode);
            return;
        }
        broadcastRoomUpdate(room);
    }

    @Override
    public List<RoomInfo> listOpenRooms() throws RemoteException {
        List<RoomInfo> openRooms = new ArrayList<>();
        for (GameRoom room : rooms.values()) {
            if (room.getStatus() == GameStatus.WAITING) {
                openRooms.add(new RoomInfo(room.getRoomCode(), room.getPlayerCount(), room.getStatus()));
            }
        }
        return Collections.unmodifiableList(openRooms);
    }

    private void broadcastRoomUpdate(GameRoom room) {
        for (String sessionId : room.getSessionIds()) {
            GameListener listener = listeners.get(sessionId);
            if (listener == null) {
                continue;
            }
            try {
                listener.onStateUpdate(room.buildState(sessionId));
            } catch (RemoteException e) {
                listeners.remove(sessionId);
            }
        }
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
