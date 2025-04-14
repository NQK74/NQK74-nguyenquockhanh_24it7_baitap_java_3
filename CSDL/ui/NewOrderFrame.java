package CSDL.ui;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NewOrderFrame extends JFrame {

    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;

    private JComboBox<CustomerComboItem> customerComboBox;
    private JComboBox<ProductComboItem> productComboBox;
    private JSpinner quantitySpinner;
    private JButton btnAddItem;
    private JButton btnRemoveItem;
    private JButton btnCreateOrder;
    private JTable orderItemsTable;
    private DefaultTableModel tableModel;
    private JLabel totalAmountLabel;

    private List<OrderItemData> orderItems;
    private double totalAmount;

    public NewOrderFrame() {
        customerDAO = new CustomerDAO();
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        orderItemDAO = new OrderItemDAO();
        orderItems = new ArrayList<>();
        totalAmount = 0.0;

        initComponents();
        loadCustomers();
        loadProducts();

        setTitle("Tạo Đơn Hàng Mới");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel customerLabel = new JLabel("Khách hàng:");
        customerComboBox = new JComboBox<>();
        customerPanel.add(customerLabel);
        customerPanel.add(customerComboBox);

        JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel productLabel = new JLabel("Sản phẩm:");
        productComboBox = new JComboBox<>();
        JLabel quantityLabel = new JLabel("Số lượng:");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        btnAddItem = new JButton("Thêm vào đơn hàng");

        productPanel.add(productLabel);
        productPanel.add(productComboBox);
        productPanel.add(quantityLabel);
        productPanel.add(quantitySpinner);
        productPanel.add(btnAddItem);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(customerPanel);
        topPanel.add(productPanel);

        String[] columnNames = {"Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderItemsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderItemsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRemoveItem = new JButton("Xóa mục đã chọn");
        btnCreateOrder = new JButton("Tạo đơn hàng");
        totalAmountLabel = new JLabel("Tổng tiền: 0 VND");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));

        buttonPanel.add(btnRemoveItem);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(totalAmountLabel);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(btnCreateOrder);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        btnAddItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOrderItem();
            }
        });

        btnRemoveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeOrderItem();
            }
        });

        btnCreateOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createOrder();
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

    private void loadProducts() {
        productComboBox.removeAllItems();
        List<Product> products = productDAO.findAll();
        for (Product product : products) {
            productComboBox.addItem(new ProductComboItem(product));
        }
    }

    private void addOrderItem() {
        if (productComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProductComboItem selectedProduct = (ProductComboItem) productComboBox.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();
        double price = selectedProduct.getProduct().getPrice();
        double itemTotal = price * quantity;

        boolean exists = false;
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItemData item = orderItems.get(i);
            if (item.getProductId() == selectedProduct.getProduct().getId()) {
                int newQuantity = item.getQuantity() + quantity;
                double newItemTotal = price * newQuantity;
                item.setQuantity(newQuantity);
                item.setItemTotal(newItemTotal);

                tableModel.setValueAt(newQuantity, i, 3);
                tableModel.setValueAt(formatCurrency(newItemTotal), i, 4);

                exists = true;
                break;
            }
        }

        if (!exists) {
            OrderItemData newItem = new OrderItemData(
                selectedProduct.getProduct().getId(),
                selectedProduct.getProduct().getName(),
                price,
                quantity,
                itemTotal
            );
            orderItems.add(newItem);

            Object[] row = new Object[]{
                selectedProduct.getProduct().getId(),
                selectedProduct.getProduct().getName(),
                formatCurrency(price),
                quantity,
                formatCurrency(itemTotal)
            };
            tableModel.addRow(row);
        }

        updateTotalAmount();

        quantitySpinner.setValue(1);
    }

    private void removeOrderItem() {
        int selectedRow = orderItemsTable.getSelectedRow();
        if (selectedRow >= 0) {
            orderItems.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            updateTotalAmount();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn mục cần xóa.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateTotalAmount() {
        totalAmount = 0.0;
        for (OrderItemData item : orderItems) {
            totalAmount += item.getItemTotal();
        }
        totalAmountLabel.setText("Tổng tiền: " + formatCurrency(totalAmount) + " VND");
    }

    private void createOrder() {
        if (customerComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm vào đơn hàng.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            CustomerComboItem selectedCustomer = (CustomerComboItem) customerComboBox.getSelectedItem();

            Order order = new Order(selectedCustomer.getCustomer().getId());
            order.setStatus("Đã tạo");

            int orderId = orderDAO.save(order);

            if (orderId > 0) {
                List<OrderItem> items = new ArrayList<>();
                for (OrderItemData itemData : orderItems) {
                    OrderItem item = new OrderItem();
                    item.setOderId(orderId);
                    item.setProductId(itemData.getProductId());
                    item.setQuantity(itemData.getQuantity());
                    items.add(item);
                }

                orderItemDAO.saveAll(items);

                orderDAO.updateTotalAmount(orderId, totalAmount);

                JOptionPane.showMessageDialog(this, "Đơn hàng đã được tạo thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

                orderItems.clear();
                tableModel.setRowCount(0);
                totalAmount = 0.0;
                updateTotalAmount();
                quantitySpinner.setValue(1);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể tạo đơn hàng.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

    private class ProductComboItem {
        private Product product;

        public ProductComboItem(Product product) {
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }

        @Override
        public String toString() {
            return product.getId() + " - " + product.getName() + " (" + formatCurrency(product.getPrice()) + " VND)";
        }
    }

    private class OrderItemData {
        private int productId;
        private String productName;
        private double price;
        private int quantity;
        private double itemTotal;

        public OrderItemData(int productId, String productName, double price, int quantity, double itemTotal) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.itemTotal = itemTotal;
        }

        public int getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getItemTotal() {
            return itemTotal;
        }

        public void setItemTotal(double itemTotal) {
            this.itemTotal = itemTotal;
        }
    }
}