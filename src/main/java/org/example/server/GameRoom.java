package org.example.server;

import org.example.common.Message;
import org.example.common.Protocol;
import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    private static List<GameRoom> rooms = new ArrayList<>();
    private List<ClientHandler> players = new ArrayList<>();
    private String currentSymbol = "X";
    private char[][] board = new char[15][15];

    public static synchronized GameRoom join(ClientHandler player) {
        GameRoom room = null;
        for (GameRoom r : rooms) {
            if (r.players.size() < 2) {
                room = r;
                break;
            }
        }
        if (room == null) {
            room = new GameRoom();
            rooms.add(room);
        }
        room.addPlayer(player);
        return room;
    }

    private void addPlayer(ClientHandler player) {
        players.add(player);
        player.setGameRoom(this);

        String symbol = players.size() == 1 ? "X" : "O";
        // Truyền đủ 6 tham số: type, data, row, col, sender, receiver
        Message startMsg = new Message(Protocol.START, symbol, -1, -1, "Server", player.getPlayerName());
        player.sendMessage(startMsg);

        if (players.size() == 2) {
            broadcastStatus("Lượt của người chơi X");
        }
    }

    public void removePlayer(ClientHandler player) {
        players.remove(player);
        broadcastStatus(player.getPlayerName() + " đã rời phòng.");
    }

    public void broadcastMove(Message msg) {
        board[msg.getRow()][msg.getCol()] = msg.getData().charAt(0);

        for (ClientHandler p : players) {
            p.sendMessage(msg);
        }

        if (checkWin(msg.getRow(), msg.getCol(), msg.getData())) {
            Message endMsg = new Message(Protocol.END, msg.getSender(), -1, -1, "Server", null);
            for (ClientHandler p : players) p.sendMessage(endMsg);
            return;
        }

        currentSymbol = currentSymbol.equals("X") ? "O" : "X";
        broadcastStatus("Lượt của người chơi " + currentSymbol);
    }

    private void broadcastStatus(String status) {
        Message statusMsg = new Message(Protocol.STATUS, status, -1, -1, "Server", null);
        for (ClientHandler p : players) p.sendMessage(statusMsg);
    }

    private boolean checkWin(int row, int col, String symbol) {
        char s = symbol.charAt(0);
        return checkDirection(row, col, s, 1, 0) ||
                checkDirection(row, col, s, 0, 1) ||
                checkDirection(row, col, s, 1, 1) ||
                checkDirection(row, col, s, 1, -1);
    }

    private boolean checkDirection(int row, int col, char s, int dr, int dc) {
        int count = 1;
        int r = row + dr, c = col + dc;
        while (inBounds(r,c) && board[r][c]==s) { count++; r+=dr; c+=dc; }
        r = row - dr; c = col - dc;
        while (inBounds(r,c) && board[r][c]==s) { count++; r-=dr; c-=dc; }
        return count >= 5;
    }

    private boolean inBounds(int r, int c) {
        return r>=0 && r<15 && c>=0 && c<15;
    }
}
