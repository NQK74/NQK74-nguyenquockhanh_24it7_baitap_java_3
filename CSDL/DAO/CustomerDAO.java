package DAO;

import DBConnect.DBConnection;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public Customer findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Customer customer = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM customer WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                customer = new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
        return customer;
    }

    public List<Customer> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Customer> customers = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM customer";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
        return customers;
    }

    public void insert(Customer customer) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO customer(name, phone) VALUES(?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            System.out.println("Da luu khach hang " + customer.getName() + " voi Id " + customer.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, null);
        }
    }

    public void update(Customer customer) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE customer SET name = ?, phone = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setInt(3, customer.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            System.out.println("Da cap nhat khach hang " + customer.getName() + " voi Id " + customer.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, null);
        }
    }
}