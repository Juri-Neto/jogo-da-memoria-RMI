package com.memoria.rmi.server.model;

import java.io.Serializable;

public class CardState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int index;
    private final String symbol;
    private final boolean faceUp;
    private final boolean matched;

    public CardState(int index, String symbol, boolean faceUp, boolean matched) {
        this.index = index;
        this.symbol = symbol;
        this.faceUp = faceUp;
        this.matched = matched;
    }

    public int getIndex() {
        return index;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public boolean isMatched() {
        return matched;
    }
}
