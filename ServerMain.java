import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {
    private static final int PORT = 12345;
    private static final List<GameRoom> rooms = new ArrayList<>();
    private static int nextRoomId = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Caro Server đang chạy trên cổng " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client mới: " + clientSocket.getInetAddress());

                GameRoom room = findAvailableRoom();

                // tính kí hiệu dựa trên số người hiện tại trong phòng:
                int count = room.getPlayerCount();
                char symbol = (count == 0) ? 'X' : 'O';

                ClientHandler handler;
                try {
                    handler = new ClientHandler(clientSocket, room, symbol);
                } catch (IOException ioe) {
                    System.out.println("Không tạo được handler: " + ioe.getMessage());
                    clientSocket.close();
                    continue;
                }

                // Gán vào phòng trước khi start thread (để room có players ngay lập tức)
                room.addPlayer(handler);

                new Thread(handler).start();

                if (room.isFull()) {
                    System.out.println("Phòng #" + room.getRoomId() + " đủ người chơi.");
                    room.broadcastMessage("Trò chơi bắt đầu!");
                    room.broadcastBoard();
                } else {
                    // nếu mới có 1 người, gửi board/notify cho người đó (tùy muốn)
                    room.broadcastMessage("Đang chờ người chơi thứ 2...");
                    room.broadcastBoard();
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static GameRoom findAvailableRoom() {
        for (GameRoom room : rooms) {
            if (!room.isFull()) return room;
        }
        GameRoom newRoom = new GameRoom(nextRoomId++);
        rooms.add(newRoom);
        return newRoom;
    }
}
