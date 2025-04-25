package TCP.baitap;

import TCP.demo.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Server {
    private static final int PORT = 12345;
    private static HashSet<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server đang chạy...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Lỗi server: " + e.getMessage());
        }
    }

    public static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run(){
            try{
                // lay luong vao ra tu socket
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (writers) {
                    writers.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Nhan : " + message);
                    broadcast(message);
                }

            }catch (IOException e){
                System.out.println("Lỗi khi tạo luồng: " + e.getMessage());
            }finally {
                if(out!= null){
                    synchronized (writers) {
                        writers.remove(out);
                    }
                }
                try{
                    socket.close();
                }catch (IOException e){
                    System.out.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        private void broadcast(String message) {
            synchronized (writers) {
                for (PrintWriter writer : writers) {
                    writer.println(message);
                }
            }
        }

    }
}
