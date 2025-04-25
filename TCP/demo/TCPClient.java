package TCP.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
    public static void main(String[] args) {
        try {
            // Thông tin kết nối
            String serverAddress = "localhost";
            int serverPort = 7432;

            // Tạo socket kết nối đến server
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Đã kết nối đến server " + serverAddress + ":" + serverPort);

            // Tạo luồng đọc và ghi
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Gửi tin nhắn đến server
            String message = "Xin chào từ client!";
            out.println(message);
            System.out.println("Đã gửi: " + message);

            // Nhận phản hồi từ server
            String response = in.readLine();
            System.out.println("Phản hồi từ server: " + response);

            // Đóng kết nối
            socket.close();
            System.out.println("Kết nối đã đóng");

        } catch (UnknownHostException e) {
            System.err.println("Không thể kết nối đến host: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Lỗi IO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
