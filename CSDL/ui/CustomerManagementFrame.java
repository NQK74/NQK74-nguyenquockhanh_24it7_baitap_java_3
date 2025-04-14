package ui;

import DAO.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CustomerManagementFrame extends JFrame {

    private CustomerDAO customerDAO;
    private JTable customersTable;
    private DefaultTableModel tableModel;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtId;
    private JButton btnSave;
    private JButton btnClear;
    private JButton btnRefresh;

    public CustomerManagementFrame() {
        customerDAO = new CustomerDAO();
        initComponents();
        loadCustomerData();
        setTitle("Quản Lý Khách Hàng");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        // Main panel using BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form components
        JLabel lblTitle = new JLabel("THÔNG TIN KHÁCH HÀNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblId = new JLabel("ID:");
        JLabel lblName = new JLabel("Tên Khách Hàng:");
        JLabel lblPhone = new JLabel("Số Điện Thoại:");

        txtId = new JTextField(20);
        txtId.setEditable(false);
        txtName = new JTextField(20);
        txtPhone = new JTextField(20);

        btnSave = new JButton("Lưu");
        btnClear = new JButton("Xóa Form");
        btnRefresh = new JButton("Làm Mới");

        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(lblTitle, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblId, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblName, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblPhone, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtPhone, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Table panel
        String[] columnNames = {"ID", "Tên", "Số Điện Thoại"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customersTable);

        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Add event listeners
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCustomer();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCustomerData();
            }
        });

        customersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = customersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtPhone.setText(tableModel.getValueAt(selectedRow, 2).toString());
                }
            }
        });
    }

    private void loadCustomerData() {
        // Clear existing table data
        tableModel.setRowCount(0);

        // Get all customers from DAO
        List<Customer> customers = customerDAO.findAll();

        // Add each customer to the table
        for (Customer customer : customers) {
            Object[] row = new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getPhone()
            };
            tableModel.addRow(row);
        }
    }

    private void saveCustomer() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();

        // Validate input
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin khách hàng.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String idText = txtId.getText().trim();
            Customer customer;

            if (idText.isEmpty()) {
                // Create new customer
                customer = new Customer();
                customer.setName(name);
                customer.setPhone(phone);
                customerDAO.insert(customer);
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing customer
                int id = Integer.parseInt(idText);
                customer = new Customer(id, name, phone);
                customerDAO.update(customer);
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

            // Refresh table and clear form
            loadCustomerData();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        customersTable.clearSelection();
    }
}