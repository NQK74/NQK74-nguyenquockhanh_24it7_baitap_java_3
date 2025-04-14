package DAO;

import DBConnect.DBConnection;
import model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public Product findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Product product = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM products WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
        return product;
    }

    public List<Product> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Product> productList = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM products";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                );
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResources(conn, stmt, rs);
        }
        return productList;
    }
}