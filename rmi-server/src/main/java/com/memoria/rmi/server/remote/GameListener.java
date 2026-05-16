package com.memoria.rmi.server.remote;

import com.memoria.rmi.server.model.GameState;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameListener extends Remote {
    void onStateUpdate(GameState state) throws RemoteException;
}
