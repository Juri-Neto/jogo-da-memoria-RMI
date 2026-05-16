package com.memoria.rmi.server.remote;

import com.memoria.rmi.server.model.GameState;
import com.memoria.rmi.server.model.JoinResult;
import com.memoria.rmi.server.model.RoomInfo;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameServer extends Remote {
    JoinResult createRoom(String playerName) throws RemoteException;
    JoinResult joinRoom(String roomCode, String playerName) throws RemoteException;
    void registerListener(String sessionId, GameListener listener) throws RemoteException;
    GameState getGameState(String sessionId) throws RemoteException;
    void flipCard(String sessionId, int cardIndex) throws RemoteException;
    void leaveGame(String sessionId) throws RemoteException;
    List<RoomInfo> listOpenRooms() throws RemoteException;
}
