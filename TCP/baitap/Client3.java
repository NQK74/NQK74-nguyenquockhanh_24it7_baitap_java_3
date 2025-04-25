package TCP.baitap;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client3 {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField messageField;
    private JTextField ipField;
    private JTextField portField;
    private JTextField nameField; // Thêm trường nhập tên
    private JButton connectButton;
    private JButton sendButton;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String userName; // Lưu tên người dùng

    public Client3() {
        // Tạo giao diện Swing
        frame = new JFrame("Chat Client");
        textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
        messageField = new JTextField(30);
        ipField = new JTextField("localhost", 15);
        portField = new JTextField("12345", 5);
        nameField = new JTextField("Người dùng", 15); // Trường nhập tên
        connectButton = new JButton("Kết nối");
        sendButton = new JButton("Gửi");

        // Bố cục giao diện
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("IP:"));
        topPanel.add(ipField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(new JLabel("Tên:"));
        topPanel.add(nameField);
        topPanel.add(connectButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // Xử lý sự kiện nút "Kết nối"
        connectButton.addActionListener(e -> {
            try {
                String ip = ipField.getText();
                int port = Integer.parseInt(portField.getText());
                userName = nameField.getText().trim(); // Lưu tên người dùng
                if (userName.isEmpty()) {
                    userName = "Người dùng";
                }
                nameField.setEditable(false); // Không cho chỉnh sửa tên sau khi kết nối
                connectToServer(ip, port);
                connectButton.setEnabled(false);
            } catch (Exception ex) {
                textArea.append("Lỗi kết nối: " + ex.getMessage() + "\n");
            }
        });

        // Xử lý sự kiện nút "Gửi"
        sendButton.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty() && out != null) {
                // Gửi tin nhắn kèm tên: Tên: Nội dung
                out.println(userName + ": " + message);
                messageField.setText("");
            }
        });

        // Gửi tin nhắn khi nhấn Enter
        messageField.addActionListener(e -> sendButton.doClick());
    }

    private void connectToServer(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Luồng đọc tin nhắn từ server
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    textArea.append(message + "\n");
                }
            } catch (IOException e) {
                textArea.append("Lỗi nhận tin: " + e.getMessage() + "\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}