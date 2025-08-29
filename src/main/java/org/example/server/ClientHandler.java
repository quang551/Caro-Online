package org.example.server;

import org.example.common.Message;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String playerName;
    private GameRoom room;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void sendMessage(Message msg) {
        try { out.writeObject(msg); out.flush(); } catch (IOException e) { e.printStackTrace(); }
    }

    public void setGameRoom(GameRoom room) { this.room = room; }
    public String getPlayerName() { return playerName; }

    @Override
    public void run() {
        try {
            Message joinMsg = (Message) in.readObject();
            playerName = joinMsg.getSender();
            System.out.println(playerName + " đã tham gia.");

            room = GameRoom.join(this);

            while (true) {
                Message msg = (Message) in.readObject();
                room.broadcastMove(msg);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(playerName + " đã rời trò chơi.");
            if (room != null) room.removePlayer(this);
        }
    }
}
