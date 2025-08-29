package org.example.client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class ClientMain {
    public static void main(String[] args) {
        try {
            // Khởi động Web UI server
            WebServer.startServer();

            // Chạy console hoặc web để nhập tên & connect server game
            String playerName = JOptionPane.showInputDialog("Nhập tên của bạn:");
            String host = JOptionPane.showInputDialog("Nhập địa chỉ server (vd: localhost):");
            int port = 5000;

            Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Gửi JOIN
            org.example.common.Message joinMsg =
                    new org.example.common.Message(org.example.common.Protocol.JOIN,
                            playerName, -1, -1, "");
            out.writeObject(joinMsg);
            out.flush();

            AtomicReference<Character> mySymbol = new AtomicReference<>(' ');

            // Chạy WebUI thay vì GameUI
            new WebUi(out, in, playerName, mySymbol); // <-- cần tạo WebUI.java

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
