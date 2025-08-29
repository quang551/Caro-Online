package org.example.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerMain {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Caro Server đang chạy...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client mới kết nối: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
