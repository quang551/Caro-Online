import java.util.*;

public class GameRoom {
    private final int roomId;
    private final char[][] board;
    private final List<ClientHandler> players;
    private int currentPlayerIndex = 0; // 0 = X, 1 = O

    public GameRoom(int roomId) {
        this.roomId = roomId;
        this.board = new char[15][15];
        for (int i = 0; i < 15; i++) Arrays.fill(board[i], '.');
        this.players = new ArrayList<>();
    }

    public int getRoomId() {
        return roomId;
    }

    public synchronized void addPlayer(ClientHandler player) {
        if (players.size() < 2) {
            players.add(player);
        }
    }

    public synchronized boolean isFull() {
        return players.size() == 2;
    }

    public synchronized int getPlayerCount() {
        return players.size();
    }

    public synchronized List<ClientHandler> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Thực hiện nước đi; trả về true nếu hợp lệ và đã cập nhật bàn cờ.
     */
    public synchronized boolean makeMove(int x, int y, char symbol) {
        // nếu chưa đủ người thì không cho đặt
        if (players.size() < 2) return false;

        // kiểm tra đến lượt
        if (players.get(currentPlayerIndex).getSymbol() != symbol) {
            return false;
        }

        if (x < 0 || x >= 15 || y < 0 || y >= 15) return false;
        if (board[x][y] != '.') return false;

        board[x][y] = symbol;

        // kiểm tra thắng trước khi chuyển lượt (tùy chọn)
        if (checkWin(x, y, symbol)) {
            broadcastBoard();
            broadcastMessage("Người chơi " + symbol + " thắng!");
            return true;
        }

        // chuyển lượt
        currentPlayerIndex = (currentPlayerIndex + 1) % 2;

        // gửi cập nhật bàn cờ
        broadcastBoard();
        return true;
    }

    // public để ServerMain hoặc ClientHandler gọi được
    public void broadcastMessage(String msg) {
        for (ClientHandler c : players) {
            try {
                c.sendMessage(msg);
            } catch (Exception e) {
                // ignore send error
            }
        }
    }

    // public để ServerMain gọi được
    public void broadcastBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("BOARD_START\n");
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                sb.append(board[i][j]);
                if (j < 14) sb.append(' ');
            }
            sb.append('\n');
        }
        sb.append("BOARD_END");
        String payload = sb.toString();

        for (ClientHandler c : players) {
            try {
                c.sendMessage(payload);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private boolean checkWin(int x, int y, char symbol) {
        return (count(x, y, 1, 0, symbol) + count(x, y, -1, 0, symbol) >= 4 || // ngang
                count(x, y, 0, 1, symbol) + count(x, y, 0, -1, symbol) >= 4 || // dọc
                count(x, y, 1, 1, symbol) + count(x, y, -1, -1, symbol) >= 4 || // chéo \
                count(x, y, 1, -1, symbol) + count(x, y, -1, 1, symbol) >= 4); // chéo /
    }

    private int count(int x, int y, int dx, int dy, char symbol) {
        int cnt = 0;
        int i = x + dx, j = y + dy;
        while (i >= 0 && i < 15 && j >= 0 && j < 15 && board[i][j] == symbol) {
            cnt++;
            i += dx;
            j += dy;
        }
        return cnt;
    }
}
