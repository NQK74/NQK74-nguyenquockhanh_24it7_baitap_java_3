package TCP.demo;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPChatServer {
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try {
            // Định nghĩa cổng lắng nghe
            int port = 7435;
            // Tạo server socket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Chat Server đang lắng nghe tại cổng " + port);

            while (true) {
                // Chấp nhận kết nối từ client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Người dùng mới kết nối từ " + clientSocket.getInetAddress());

                // Tạo luồng xử lý cho client mới
                ChatClientHandler clientHandler = new ChatClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.err.println("Lỗi IO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Lớp xử lý client chat
    private static class ChatClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String userName;

        public ChatClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Tạo luồng đọc và ghi
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Lấy tên người dùng
                userName = in.readLine();
                System.out.println(userName + " đã tham gia cuộc trò chuyện");

                // Thông báo cho tất cả người dùng
                broadcastMessage("SERVER: " + userName + " đã tham gia cuộc trò chuyện");

                // Thêm writer cho client này vào danh sách
                clientWriters.add(out);

                // Xử lý tin nhắn
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equals("EXIT")) {
                        break;
                    }
                    broadcastMessage(userName + ": " + message);
                }

            } catch (IOException e) {
                System.err.println("Lỗi xử lý client: " + e.getMessage());
            } finally {
                // Đóng kết nối và dọn dẹp khi client ngắt kết nối
                if (out != null) {
                    clientWriters.remove(out);
                }
                if (userName != null) {
                    System.out.println(userName + " đã rời cuộc trò chuyện");
                    broadcastMessage("SERVER: " + userName + " đã rời cuộc trò chuyện");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Gửi tin nhắn đến tất cả người dùng
        private void broadcastMessage(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}