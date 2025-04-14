package DBConnect;

import java.sql.*;

public class DBConnection {
    public static final String url = "jdbc:mysql://localhost/order_management";
    private static final String user = "root";
    private static final String password = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver không tìm thấy", e);
        }
    }

    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }
}