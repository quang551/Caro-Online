package org.example.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicReference;

public class WebUi {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String playerName;
    private AtomicReference<Character> mySymbol;

    // Constructor đầy đủ
    public WebUi(ObjectOutputStream out, ObjectInputStream in, String playerName, AtomicReference<Character> mySymbol) {
        this.out = out;
        this.in = in;
        this.playerName = playerName;
        this.mySymbol = mySymbol;
    }

    public void sendMove(int row, int col) {
        try {
            out.writeObject(new String[]{"MOVE", String.valueOf(row), String.valueOf(col)});
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateBoard(int row, int col, String symbol) {
        // Có thể dùng WebSocket hoặc REST API gửi về trình duyệt.
        System.out.println("Cập nhật ô: " + row + "," + col + " -> " + symbol);
    }

    public void updateStatus(String status) {
        System.out.println("Trạng thái: " + status);
    }

}
