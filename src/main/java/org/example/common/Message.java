package org.example.common;

import java.io.Serializable;

public class Message implements Serializable {
    private int type;
    private String data;
    private int row;
    private int col;
    private String sender;
    private String receiver;

    public Message(int type, String data, int row, int col, String sender, String receiver) {
        this.type = type;
        this.data = data;
        this.row = row;
        this.col = col;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message(int join, String playerName, int row, int col, String sender) {
    }

    public int getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
