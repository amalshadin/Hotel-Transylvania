package com.hotelbooking.main.db;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

public class DatabaseHelper {
    private static final String URL = "jdbc:postgresql://ep-tiny-snow-ady72ezi-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_8GAe5PjcpBnY";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    static {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "full_name TEXT NOT NULL, " +
                "phone TEXT)";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Table 'users' is ready.");
        } catch (SQLException e) {
            System.err.println("❌ Error setting up database table: " + e.getMessage());
        }

        String bookingsSql = "CREATE TABLE IF NOT EXISTS bookings (" +
                "booking_id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL REFERENCES users(id), " + // Links to the user
                "room_type TEXT NOT NULL, " +
                "check_in_date DATE NOT NULL, " +
                "check_out_date DATE NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'Confirmed')"; // e.g., Confirmed, Canceled
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(bookingsSql);
            System.out.println("✅ Table 'bookings' is ready.");
        } catch (SQLException e) {
            System.err.println("❌ Error setting up database table: " + e.getMessage());
        }


    }

    // Removed email parameter
    public boolean registerUser(String username, String password, String fullName, String phone) {
        String sql = "INSERT INTO users(username, password, full_name, phone) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, fullName);
            pstmt.setString(4, phone);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    public boolean validateLogin(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (storedHash == null) return false;
                return BCrypt.checkpw(password, storedHash);
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Login validation error: " + e.getMessage());
            return false;
        }
    }

    public Map<String, String> getUserDetails(String username) {
        String sql = "SELECT full_name, phone FROM users WHERE username = ?";
        Map<String, String> userDetails = new HashMap<>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userDetails.put("fullName", rs.getString("full_name"));
                userDetails.put("phone", rs.getString("phone"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user details: " + e.getMessage());
        }
        return userDetails;
    }

    public boolean verifyCurrentUserPassword(String username, String password) {
        return validateLogin(username, password);
    }

    public boolean changePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            pstmt.setString(1, newHashedPassword);
            pstmt.setString(2, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Password change error: " + e.getMessage());
            return false;
        }
    }
    /**
     * Counts how many rooms of a specific type are booked for a given date range.
     * This checks for any overlap between existing bookings and the desired stay.
     * @param roomType The type of room (e.g., "Deluxe AC King").
     * @param checkInDate The desired check-in date.
     * @param nights The number of nights to stay.
     * @return The number of rooms already booked.
     */
    public int getBookingCountForDateRange(String roomType, java.util.Date checkInDate, int nights) {
        long checkInTime = checkInDate.getTime();
        long checkOutTime = checkInTime + TimeUnit.DAYS.toMillis(nights);

        java.sql.Date sqlCheckIn = new java.sql.Date(checkInTime);
        java.sql.Date sqlCheckOut = new java.sql.Date(checkOutTime);

        // FIX: Added "status = 'Confirmed'" to only count active bookings.
        String sql = "SELECT COUNT(*) FROM bookings WHERE room_type = ? AND " +
                "status = 'Confirmed' AND " +
                "check_in_date < ? AND check_out_date > ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomType);
            pstmt.setDate(2, sqlCheckOut); // An existing booking starts before the new one ends
            pstmt.setDate(3, sqlCheckIn);  // And it ends after the new one begins

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting bookings: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Creates a new booking record in the database.
     * FIX: Changed the third parameter from java.sql.Date to java.util.Date.
     */
    public boolean createBooking(String username, String roomType, java.util.Date checkInDate, int nights) {
        String sql = "INSERT INTO bookings (user_id, room_type, check_in_date, check_out_date) " +
                "VALUES ((SELECT id FROM users WHERE username = ?), ?, ?, ?)";

        long checkInTime = checkInDate.getTime();
        long checkOutTime = checkInTime + TimeUnit.DAYS.toMillis(nights);

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, roomType);
            // Conversion to java.sql.Date happens here
            pstmt.setDate(3, new java.sql.Date(checkInTime));
            pstmt.setDate(4, new java.sql.Date(checkOutTime));

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            return false;
        }
    }
    /**
     * Fetches all booking records for a specific user from the database.
     * @param username The username of the logged-in user.
     * @return A List of Object arrays, where each array represents a booking row.
     */
    public List<Object[]> getBookingsByUsername(String username) {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.room_type, b.check_in_date, b.check_out_date, b.status " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "WHERE u.username = ? " +
                "ORDER BY b.check_in_date DESC"; // Show most recent bookings first

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookings.add(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("room_type"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings: " + e.getMessage());
        }
        return bookings;
    }

    /**
     * Updates a booking's status to 'Canceled' in the database.
     * @param bookingId The ID of the booking to cancel.
     * @return true if the cancellation was successful, false otherwise.
     */
    public boolean cancelBookingById(int bookingId) {
        String sql = "UPDATE bookings SET status = 'Canceled' WHERE booking_id = ? AND status = 'Confirmed'";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error canceling booking: " + e.getMessage());
            return false;
        }
    }
}
