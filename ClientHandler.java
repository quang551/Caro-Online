import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final char symbol;
    private final GameRoom room;

    public ClientHandler(Socket socket, GameRoom room, char symbol) throws IOException {
        this.socket = socket;
        this.room = room;
        this.symbol = symbol;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sendMessage("Bạn là người chơi: " + symbol);
    }

    @Override
    public void run() {
        try {
            String input;
            while ((input = in.readLine()) != null) {
                input = input.trim();
                if (input.isEmpty()) continue;

                // nếu client gửi lệnh dạng "x,y"
                if (input.matches("\\d+\\s*,\\s*\\d+")) {
                    String[] parts = input.split("\\s*,\\s*");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);

                    boolean ok = room.makeMove(x, y, symbol);
                    if (!ok) {
                        sendMessage("Nước đi không hợp lệ hoặc chưa đến lượt bạn!");
                    }
                } else {
                    // các lệnh khác: in thẳng ra
                    sendMessage("Unknown command: " + input);
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignore) {}
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public char getSymbol() {
        return symbol;
    }
}
