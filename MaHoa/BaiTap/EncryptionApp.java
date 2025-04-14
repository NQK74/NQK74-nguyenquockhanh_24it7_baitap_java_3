package MaHoa.BaiTap;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EncryptionApp extends JFrame {
    private Encryptable encryptor;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JComboBox<String> algorithmComboBox;
    private JComboBox<String> operationComboBox;
    private JButton processButton;
    private JButton generateKeyButton;
    private JTextArea keyTextArea;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private JTabbedPane tabbedPane;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private final java.util.Map<String, String> userDatabase = new java.util.HashMap<>();

    public EncryptionApp() {
        setTitle("Ứng dụng Mã hóa");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Đăng nhập", createLoginPanel());
        tabbedPane.addTab("Mã hóa/Giải mã", createEncryptionPanel());

        add(tabbedPane);

        try {
            encryptor = new AESEncryption();
            updateKeyDisplay();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        try {
            userDatabase.put("admin", HashUtils.hashSHA256("admin123"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        loginButton = new JButton("Đăng nhập");
        formPanel.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        statusLabel = new JLabel("Vui lòng đăng nhập (admin/admin123)");
        formPanel.add(statusLabel, gbc);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(formPanel);
        panel.add(centerPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                executorService.submit(() -> {
                    try {
                        boolean success = authenticate(username, password);

                        SwingUtilities.invokeLater(() -> {
                            if (success) {
                                statusLabel.setText("Đăng nhập thành công!");
                                statusLabel.setForeground(new Color(0, 150, 0));
                                tabbedPane.setSelectedIndex(1);
                            } else {
                                statusLabel.setText("Đăng nhập thất bại. Vui lòng thử lại.");
                                statusLabel.setForeground(Color.RED);
                            }
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Lỗi: " + ex.getMessage());
                            statusLabel.setForeground(Color.RED);
                        });
                    }
                });
            }
        });

        return panel;
    }

    private boolean authenticate(String username, String password) throws NoSuchAlgorithmException {
        String hashedPassword = HashUtils.hashSHA256(password);

        String storedHash = userDatabase.get(username);
        return storedHash != null && storedHash.equals(hashedPassword);
    }

    private JPanel createEncryptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        controlPanel.add(new JLabel("Thuật toán:"));
        algorithmComboBox = new JComboBox<>(new String[]{"AES", "RSA"});
        controlPanel.add(algorithmComboBox);

        controlPanel.add(new JLabel("Hoạt động:"));
        operationComboBox = new JComboBox<>(new String[]{"Mã hóa", "Giải mã"});
        controlPanel.add(operationComboBox);

        processButton = new JButton("Xử lý");
        controlPanel.add(processButton);

        generateKeyButton = new JButton("Tạo khóa mới");
        controlPanel.add(generateKeyButton);

        panel.add(controlPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Văn bản đầu vào"));
        inputTextArea = new JTextArea();
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Kết quả"));
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        centerPanel.add(inputPanel);
        centerPanel.add(outputPanel);

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khóa"));
        keyTextArea = new JTextArea(5, 50);
        keyTextArea.setEditable(false);
        keyPanel.add(new JScrollPane(keyTextArea), BorderLayout.CENTER);

        panel.add(keyPanel, BorderLayout.SOUTH);

        algorithmComboBox.addActionListener(e -> {
            try {
                changeAlgorithm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        generateKeyButton.addActionListener(e -> {
            try {
                changeAlgorithm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi tạo khóa: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        processButton.addActionListener(e -> processData());

        return panel;
    }

    private void changeAlgorithm() throws Exception {
        String encryptedData = outputTextArea.getText();

        String algorithm = (String) algorithmComboBox.getSelectedItem();
        if ("AES".equals(algorithm)) {
            if (!(encryptor instanceof AESEncryption)) {
                encryptor = new AESEncryption();
            }
        } else {
            if (!(encryptor instanceof RSAEncryption)) {
                encryptor = new RSAEncryption();
            }
        }
        updateKeyDisplay();
    }

    private void updateKeyDisplay() {
        keyTextArea.setText("");

        if (encryptor instanceof AESEncryption) {
            AESEncryption aes = (AESEncryption) encryptor;
            keyTextArea.append("AES Key (Base64):\n" + aes.getKeyAsBase64());
        } else if (encryptor instanceof RSAEncryption) {
            RSAEncryption rsa = (RSAEncryption) encryptor;
            keyTextArea.append("RSA Public Key (Base64):\n" + rsa.getPublicKeyString());
            keyTextArea.append("\n\nRSA Private Key (Base64):\n" + rsa.getPrivateKeyString());
        }
    }

    private void processData() {
        String input = inputTextArea.getText();
        String operation = (String) operationComboBox.getSelectedItem();
        String algorithm = (String) algorithmComboBox.getSelectedItem();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập văn bản.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if ("RSA".equals(algorithm) && input.getBytes(StandardCharsets.UTF_8).length > 245
            && "Mã hóa".equals(operation)) {
            JOptionPane.showMessageDialog(this,
                "Văn bản quá dài để mã hóa RSA. Vui lòng sử dụng AES cho văn bản dài.",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập văn bản.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        executorService.submit(() -> {
            try {
                String result;
                if ("Mã hóa".equals(operation)) {
                    result = encryptor.encrypt(input);
                } else {
                    try {
                        Base64.getDecoder().decode(input);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Dữ liệu đầu vào không phải định dạng Base64 hợp lệ");
                    }
                    result = encryptor.decrypt(input);
                }

                final String finalResult = result;
                SwingUtilities.invokeLater(() -> outputTextArea.setText(finalResult));
            } catch (IllegalArgumentException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(EncryptionApp.this,
                        "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(EncryptionApp.this,
                        "Lỗi xử lý: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EncryptionApp app = new EncryptionApp();
            app.setVisible(true);
        });
    }
}
