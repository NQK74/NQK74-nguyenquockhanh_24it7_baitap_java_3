package ui;

import DAO.CustomerDAO;
import DAO.OrderDAO;
import DAO.OrderItemDAO;
import DAO.ProductDAO;
import model.Customer;
import model.Order;
import model.OrderItem;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

public class OrderHistoryFrame extends JFrame {

    private CustomerDAO customerDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private ProductDAO productDAO;

    private JComboBox<CustomerComboItem> customerComboBox;
    private JButton btnSearch;
    private JTable ordersTable;
    private JTable orderDetailsTable;
    private DefaultTableModel ordersTableModel;
    private DefaultTableModel detailsTableModel;

    public OrderHistoryFrame() {
        customerDAO = new CustomerDAO();
        orderDAO = new OrderDAO();
        orderItemDAO = new OrderItemDAO();
        productDAO = new ProductDAO();

        initComponents();
        loadCustomers();

        setTitle("Lịch Sử Đơn Hàng");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel customerLabel = new JLabel("Chọn khách hàng:");
        customerComboBox = new JComboBox<>();
        btnSearch = new JButton("Tìm kiếm");

        customerPanel.add(customerLabel);
        customerPanel.add(customerComboBox);
        customerPanel.add(btnSearch);

        String[] orderColumns = {"Mã đơn hàng", "Ngày đặt", "Trạng thái", "Tổng tiền"};
        ordersTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách đơn hàng"));

        String[] detailColumns = {"Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
        detailsTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderDetailsTable = new JTable(detailsTableModel);
        JScrollPane detailsScrollPane = new JScrollPane(orderDetailsTable);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn hàng"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ordersScrollPane, detailsScrollPane);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        mainPanel.add(customerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        setContentPane(mainPanel);

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchOrders();
            }
        });

        ordersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                viewOrderDetails();
            }
        });
    }

    private void loadCustomers() {
        customerComboBox.removeAllItems();
        List<Customer> customers = customerDAO.findAll();
        for (Customer customer : customers) {
            customerComboBox.addItem(new CustomerComboItem(customer));
        }
    }

    private void searchOrders() {
        ordersTableModel.setRowCount(0);
        detailsTableModel.setRowCount(0);

        if (customerComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CustomerComboItem selectedCustomer = (CustomerComboItem) customerComboBox.getSelectedItem();
        int customerId = selectedCustomer.getCustomer().getId();

        List<Order> orders = orderDAO.findByCustomerId(customerId);

        if (orders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng nào cho khách hàng này.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Order order : orders) {
            Object[] row = new Object[] {
                order.getId(),
                order.getOderDate(),
                order.getStatus(),
                formatCurrency(order.getTotalAmount()) + " VND"
            };
            ordersTableModel.addRow(row);
        }
    }

    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow >= 0) {
            detailsTableModel.setRowCount(0);

            int orderId = Integer.parseInt(ordersTableModel.getValueAt(selectedRow, 0).toString());

            List<OrderItem> items = orderItemDAO.findByOrderId(orderId);

            for (OrderItem item : items) {
                Product product = productDAO.findById(item.getProductId());
                if (product != null) {
                    double itemTotal = product.getPrice() * item.getQuantity();
                    Object[] row = new Object[] {
                        product.getId(),
                        product.getName(),
                        formatCurrency(product.getPrice()),
                        item.getQuantity(),
                        formatCurrency(itemTotal)
                    };
                    detailsTableModel.addRow(row);
                }
            }
        }
    }

    private String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    private class CustomerComboItem {
        private Customer customer;

        public CustomerComboItem(Customer customer) {
            this.customer = customer;
        }

        public Customer getCustomer() {
            return customer;
        }

        @Override
        public String toString() {
            return customer.getId() + " - " + customer.getName() + " (" + customer.getPhone() + ")";
        }
    }
}