package TCP.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) {
        try {
            // Định nghĩa cổng lắng nghe
            int port = 7432;
            // Tạo server socket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server đang lắng nghe tại cổng " + port);

            while (true) {
                // Chấp nhận kết nối từ client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Kết nối được thiết lập từ " + clientSocket.getInetAddress());

                // Tạo luồng đọc và ghi
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Đọc tin nhắn từ client
                String message = in.readLine();
                System.out.println("Client: " + message);

                // Gửi phản hồi
                String response = "Server đã nhận tin nhắn: " + message;
                out.println(response);

                // Đóng kết nối
                clientSocket.close();
                System.out.println("Kết nối đã đóng");

                // Thoát khỏi vòng lặp sau khi phục vụ một client
                // (Bỏ lệnh break để tiếp tục phục vụ các client khác)
                // break;
            }

            // serverSocket.close(); // Đóng server socket khi không cần nữa
        } catch (IOException e) {
            System.err.println("Lỗi IO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
