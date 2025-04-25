package TCP.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // Tạo luồng đọc và ghi
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Đọc tin nhắn từ client
            String message = in.readLine();
            System.out.println("Client " + clientSocket.getInetAddress() + ": " + message);

            // Xử lý và trả về phản hồi
            String response = "Server đã nhận tin nhắn: " + message;
            out.println(response);

            // Đóng kết nối
            clientSocket.close();
            System.out.println("Kết nối với " + clientSocket.getInetAddress() + " đã đóng");

        } catch (IOException e) {
            System.err.println("Lỗi xử lý client: " + e.getMessage());
        }
    }
}

