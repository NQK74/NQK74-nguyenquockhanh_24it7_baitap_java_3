package TCP.demo;

import java.io.*;
import java.net.*;

public class TCPChatClient {
    public static void main(String[] args) {
        try {
            // Thông tin kết nối
            String serverAddress = "localhost";
            int serverPort = 7435;
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Nhập tên người dùng: ");
            String userName = consoleReader.readLine();

            // Tạo socket kết nối đến server
            Socket socket = new Socket(serverAddress, serverPort);

            // Tạo luồng đọc và ghi
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Gửi tên người dùng
            out.println(userName);

            // Tạo luồng để lắng nghe tin nhắn từ server
            ServerListener serverListener = new ServerListener(socket);
            new Thread(serverListener).start();

            // Luồng chính đọc tin nhắn từ console và gửi đến server
            String message;
            System.out.println("Bắt đầu trò chuyện (nhập 'EXIT' để thoát):");
            while ((message = consoleReader.readLine()) != null) {
                out.println(message);
                if (message.equals("EXIT")) {
                    break;
                }
            }

            // Đóng kết nối
            socket.close();

        } catch (UnknownHostException e) {
            System.err.println("Không thể kết nối đến host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Lỗi IO: " + e.getMessage());
        }
    }

    // Lớp lắng nghe tin nhắn từ server
    private static class ServerListener implements Runnable {
        private Socket socket;
        private BufferedReader in;

        public ServerListener(Socket socket) {
            this.socket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.err.println("Mất kết nối với server: " + e.getMessage());
                }
            }
        }
    }
}