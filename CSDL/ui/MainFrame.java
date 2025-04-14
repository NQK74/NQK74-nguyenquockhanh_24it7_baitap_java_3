package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private JButton btnCustomers;
    private JButton btnNewOrder;
    private JButton btnOrderHistory;

    public MainFrame() {
        initComponents();
        setTitle("Hệ Thống Quản Lý Đơn Hàng");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("HỆ THỐNG QUẢN LÝ ĐƠN HÀNG", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 20));

        btnCustomers = new JButton("Quản Lý Khách Hàng");
        btnNewOrder = new JButton("Tạo Đơn Hàng Mới");
        btnOrderHistory = new JButton("Xem Lịch Sử Đơn Hàng");

        btnCustomers.setFont(new Font("Arial", Font.PLAIN, 16));
        btnNewOrder.setFont(new Font("Arial", Font.PLAIN, 16));
        btnOrderHistory.setFont(new Font("Arial", Font.PLAIN, 16));

        buttonPanel.add(btnCustomers);
        buttonPanel.add(btnNewOrder);
        buttonPanel.add(btnOrderHistory);

        JPanel paddedButtonPanel = new JPanel();
        paddedButtonPanel.setLayout(new BorderLayout());
        paddedButtonPanel.add(buttonPanel, BorderLayout.CENTER);
        paddedButtonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(paddedButtonPanel, BorderLayout.CENTER);


        btnCustomers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerManagementFrame customerFrame = new CustomerManagementFrame();
                customerFrame.setVisible(true);
            }
        });

        btnNewOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CSDL.ui.NewOrderFrame orderFrame = new CSDL.ui.NewOrderFrame();
                orderFrame.setVisible(true);
            }
        });

        btnOrderHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderHistoryFrame historyFrame = new OrderHistoryFrame();
                historyFrame.setVisible(true);
            }
        });

        setContentPane(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}