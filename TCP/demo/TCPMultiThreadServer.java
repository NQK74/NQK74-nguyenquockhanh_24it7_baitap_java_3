package TCP.demo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMultiThreadServer {
    public static void main(String[] args) {
        try {
            // Định nghĩa cổng lắng nghe
            int port = 7433;
            // Tạo server socket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server đa luồng đang lắng nghe tại cổng " + port);

            while (true) {
                // Chấp nhận kết nối từ client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Kết nối mới từ " + clientSocket.getInetAddress());

                // Tạo và khởi động một luồng mới để xử lý client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.err.println("Lỗi IO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}