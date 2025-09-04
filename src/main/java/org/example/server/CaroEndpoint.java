package org.example.server;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/caro")
public class CaroEndpoint {

    private static final int SIZE = 15;
    private static final String[][] board = new String[SIZE][SIZE];

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private static Session playerX = null;
    private static Session playerO = null;
    private static String currentPlayer = "X";

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);

        String role;
        if (playerX == null) {
            playerX = session;
            role = "X";
        } else if (playerO == null) {
            playerO = session;
            role = "O";
        } else {
            role = "spectator";
        }

        // gửi role cho client mới
        sendMessage(session, "{\"type\":\"role\",\"role\":\"" + role + "\"}");
        // gửi toàn bộ bàn cờ hiện tại
        sendMessage(session, makeBoardMessage());

        System.out.println("Client " + session.getId() + " kết nối với role: " + role);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            String[] parts = message.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);

            // check hợp lệ
            if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
                sendMessage(session, "{\"type\":\"error\",\"msg\":\"Nước đi không hợp lệ!\"}");
                return;
            }

            synchronized (board) {
                String role = getPlayerRole(session);

                if (!role.equals(currentPlayer)) {
                    sendMessage(session, "{\"type\":\"error\",\"msg\":\"Chưa đến lượt bạn!\"}");
                    return;
                }

                if (board[row][col] != null) {
                    sendMessage(session, "{\"type\":\"error\",\"msg\":\"Ô này đã có người đi!\"}");
                    return;
                }

                // cập nhật bàn cờ
                board[row][col] = role;

                // gửi nước đi cho tất cả client
                broadcast("{\"type\":\"move\",\"row\":" + row + ",\"col\":" + col + ",\"player\":\"" + role + "\"}");

                // kiểm tra thắng
                if (checkWin(row, col, role)) {
                    broadcast("{\"type\":\"win\",\"player\":\"" + role + "\"}");
                } else {
                    // đổi lượt
                    currentPlayer = currentPlayer.equals("X") ? "O" : "X";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(session, "{\"type\":\"error\",\"msg\":\"Lỗi xử lý dữ liệu!\"}");
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        if (session.equals(playerX)) playerX = null;
        if (session.equals(playerO)) playerO = null;
        System.out.println("Client thoát: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Lỗi từ client " + session.getId() + ": " + throwable.getMessage());
    }

    // gửi cho 1 client
    private void sendMessage(Session session, String msg) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // gửi cho tất cả
    private void broadcast(String msg) {
        System.out.println("Broadcast: " + msg);
        for (Session s : sessions) {
            sendMessage(s, msg);
        }
    }

    // role của session
    private String getPlayerRole(Session s) {
        if (s.equals(playerX)) return "X";
        if (s.equals(playerO)) return "O";
        return "spectator";
    }

    // tạo JSON bàn cờ
    private String makeBoardMessage() {
        StringBuilder sb = new StringBuilder("{\"type\":\"board\",\"data\":[");
        for (int i = 0; i < SIZE; i++) {
            sb.append("[");
            for (int j = 0; j < SIZE; j++) {
                sb.append(board[i][j] == null ? "\"\"" : "\"" + board[i][j] + "\"");
                if (j < SIZE - 1) sb.append(",");
            }
            sb.append("]");
            if (i < SIZE - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    // kiểm tra thắng
    private boolean checkWin(int row, int col, String player) {
        return (count(row, col, 1, 0, player) >= 5 ||  // ngang
                count(row, col, 0, 1, player) >= 5 ||  // dọc
                count(row, col, 1, 1, player) >= 5 ||  // chéo chính
                count(row, col, 1, -1, player) >= 5);  // chéo phụ
    }

    private int count(int row, int col, int dr, int dc, String player) {
        int cnt = 1;
        int r = row + dr, c = col + dc;
        while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && player.equals(board[r][c])) {
            cnt++;
            r += dr; c += dc;
        }
        r = row - dr; c = col - dc;
        while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && player.equals(board[r][c])) {
            cnt++;
            r -= dr; c -= dc;
        }
        return cnt;
    }
}
