package com.hotelbooking.main.db;

import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:postgresql://ep-tiny-snow-ady72ezi-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";   // 🔑 from Neon
    private static final String PASSWORD = "npg_8GAe5PjcpBnY"; // 🔑 from Neon

    // Static block → runs ONCE when the class is first loaded
    static {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL)";
            stmt.execute(sql);
            System.out.println("✅ Table ready.");
        } catch (SQLException e) {
            System.out.println("❌ Error setting up database: " + e.getMessage());
        }
    }

    // Method to get a new DB connection
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Register a user
    public boolean registerUser(String username, String password, String name) {
        String sql = "INSERT INTO users(username, password, name) VALUES(?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // ⚠️ in real apps hash this!
            pstmt.setString(2, name); // ⚠️ in real apps hash this!
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Register error: " + e.getMessage());
            return false;
        }
    }

    // Validate login
    public boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }
}
