package com.memoria.rmi.client;

import com.memoria.rmi.server.model.GameState;
import com.memoria.rmi.server.remote.GameListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GameListenerImpl extends UnicastRemoteObject implements GameListener {
    private static final long serialVersionUID = 1L;
    private final GameClient client;

    public GameListenerImpl(GameClient client) throws RemoteException {
        super();
        this.client = client;
    }

    @Override
    public void onStateUpdate(GameState state) throws RemoteException {
        client.onRemoteStateUpdate(state);
    }
}
