package org.example.client;

import org.example.common.Message;
import org.example.common.Protocol;

import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicReference;

public class ListenerThread extends Thread {
    private final ObjectInputStream in;
    private final WebUi webUI;  // UI hiển thị trên web
    private final AtomicReference<Character> mySymbol;

    public ListenerThread(ObjectInputStream in, WebUi webUI, AtomicReference<Character> mySymbol) {
        this.in = in;
        this.webUI = webUI;
        this.mySymbol = mySymbol;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Object obj = in.readObject();
                if (!(obj instanceof Message msg)) continue;

                switch (msg.getType()) {
                    case Protocol.UPDATE -> webUI.updateBoard(msg.getRow(), msg.getCol(), msg.getData());
                    case Protocol.STATUS -> webUI.updateStatus(msg.getData());
                    case Protocol.SYMBOL -> mySymbol.set(msg.getData().charAt(0));
                    default -> System.out.println("Nhận thông điệp không xác định: " + msg.getType());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi ListenerThread: " + e.getMessage());
        }
    }
}
