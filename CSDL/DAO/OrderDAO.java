package DAO;

import DBConnect.DBConnection;
import model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public Order findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Order order = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM orders WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                order = new Order(
                    rs.getInt("id"),
                    rs.getInt("customer_id"),
                    rs.getString("order_date"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
        return order;
    }

    public List<Order> findByCustomerId(int customerId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Order> orderList = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("id"),
                    rs.getInt("customer_id"),
                    rs.getString("order_date"),
                    rs.getString("status")
                );
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
        return orderList;
    }

    public int save(Order order) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO orders(customer_id, status) VALUES(?, ?)";
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getCustomerId());
            stmt.setString(2, order.getStatus());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Them don hang that bai");
            }
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
                order.setId(generatedId);
                System.out.println("Da luu don hang voi id: " + generatedId);
            } else {
                throw new SQLException("Them don hang that bai");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
        return generatedId;
    }

    public void updateTotalAmount(int orderId, double totalAmount) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE orders SET total_amount = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, totalAmount);
            stmt.setInt(2, orderId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Cap nhat that bai");
            }
            System.out.println("Da cap nhat tong tien don hang id " + orderId + ": " + totalAmount);
        } catch (SQLException e) {
            System.err.println("Loi khi cap nhat tong tien don hang: " + e.getMessage());
        } finally {
            DBConnection.closeResources(conn, stmt, null);
        }
    }
}