package DAO;

import DBConnect.DBConnection;
import model.OrderItem;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {
    private ProductDAO productDAO = new ProductDAO();

    public OrderItem findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        OrderItem orderItem = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM order_items WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                orderItem = new OrderItem(
                    rs.getInt("id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
        return orderItem;
    }

    public List<OrderItem> findByOrderId(int orderId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<OrderItem> orderItems = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM order_items WHERE order_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                OrderItem orderItem = new OrderItem(
                    rs.getInt("id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity")
                );
                orderItems.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
        return orderItems;
    }

    public void save(OrderItem orderItem) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO order_items(order_id, product_id, quantity) VALUES(?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, orderItem.getOderId());
            stmt.setInt(2, orderItem.getProductId());
            stmt.setInt(3, orderItem.getQuantity());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Them chi tiet don hang that bai");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                orderItem.setId(rs.getInt(1));
                System.out.println("Da luu chi tiet don hang voi id: " + orderItem.getId());
            } else {
                throw new SQLException("Them chi tiet don hang that bai, khong lay duoc ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
    }

    public void saveAll(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            save(item);
        }
    }

    public double calculateOrderTotal(int orderId) {
        List<OrderItem> items = findByOrderId(orderId);
        double total = 0.0;

        for (OrderItem item : items) {
            Product product = productDAO.findById(item.getProductId());
            if (product != null) {
                total += product.getPrice() * item.getQuantity();
            }
        }

        return total;
    }
}