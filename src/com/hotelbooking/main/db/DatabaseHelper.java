package com.hotelbooking.main.db;

import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DatabaseHelper {
    private static final String URL = "jdbc:postgresql://ep-tiny-snow-ady72ezi-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_8GAe5PjcpBnY";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    static {
        // --- Users Table Setup ---
        String usersSql = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "full_name TEXT NOT NULL, " +
                "phone TEXT)";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersSql);
            System.out.println("✅ Table 'users' is ready.");
        } catch (SQLException e) {
            System.err.println("❌ Error setting up 'users' table: " + e.getMessage());
        }

        // --- Bookings Table Setup ---
        String bookingsSql = "CREATE TABLE IF NOT EXISTS bookings (" +
                "booking_id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL REFERENCES users(id), " +
                "room_type TEXT NOT NULL, " +
                "check_in_date DATE NOT NULL, " +
                "check_out_date DATE NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'Confirmed')";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(bookingsSql);
            System.out.println("✅ Table 'bookings' is ready.");
        } catch (SQLException e) {
            System.err.println("❌ Error setting up 'bookings' table: " + e.getMessage());
        }

        // --- CORRECTED, NON-DESTRUCTIVE Room Types Table Setup ---
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Step 1: Create the table with the correct schema IF IT DOES NOT EXIST.
            String roomTypesSql = "CREATE TABLE IF NOT EXISTS room_types (" +
                    "type_name TEXT PRIMARY KEY, " +
                    "price_per_night NUMERIC(10, 2) NOT NULL, " +
                    "total_inventory INT NOT NULL, " +
                    "description TEXT, " +
                    "amenities TEXT)";
            stmt.execute(roomTypesSql);
            System.out.println("✅ Table 'room_types' is ready.");

            // Step 2: Check if the table is empty. If so, populate it with default data.
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM room_types");
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println(" Populating 'room_types' with default data for the first time.");
                stmt.execute("INSERT INTO room_types (type_name, price_per_night, total_inventory, description, amenities) VALUES " +
                        "('Deluxe AC King', 4500.00, 5, 'Spacious and elegant, featuring a king-sized bed and enhanced amenities.', 'Air Conditioning,Free Wi-Fi,4K Smart TV,Mini-bar,City View')," +
                        "('Executive Suite AC', 8000.00, 3, 'The pinnacle of luxury with a separate living area, kitchenette, and panoramic city views.', 'All Deluxe amenities,Living Area,Kitchenette,VIP Lounge Access')," +
                        "('Standard AC Queen', 3200.00, 8, 'A comfortable room with a queen-sized bed, perfect for business travelers or couples.', 'Air Conditioning,Free Wi-Fi,HD TV,Work Desk')," +
                        "('Standard Non-AC', 1800.00, 10, 'An affordable and clean room with a comfortable double bed.', 'Ceiling Fan,Free Wi-Fi,TV,Private Bathroom')," +
                        "('Economy Twin Non-AC', 1000.00, 6, 'Perfect for friends or colleagues, this room features two separate single beds.', 'Ceiling Fan,Free Wi-Fi,Shared Bathroom')"
                );
                System.out.println("✅ 'room_types' table populated successfully.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error setting up 'room_types' table: " + e.getMessage());
        }
    }

    // --- User Management ---

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
                return storedHash != null && BCrypt.checkpw(password, storedHash);
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
        // This method simply re-uses the validation logic for checking a user's current password.
        return validateLogin(username, password);
    }

    public boolean changePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            pstmt.setString(1, newHashedPassword);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Password change error: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> getAllUsers() {
        List<Object[]> users = new ArrayList<>();
        String sql = "SELECT id, username, full_name, phone FROM users ORDER BY id";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }
        return users;
    }


    // --- Booking Management ---

    public boolean createBooking(String username, String roomType, java.util.Date checkInDate, int nights) {
        String sql = "INSERT INTO bookings (user_id, room_type, check_in_date, check_out_date) " +
                "VALUES ((SELECT id FROM users WHERE username = ?), ?, ?, ?)";
        long checkInTime = checkInDate.getTime();
        long checkOutTime = checkInTime + TimeUnit.DAYS.toMillis(nights);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, roomType);
            pstmt.setDate(3, new java.sql.Date(checkInTime));
            pstmt.setDate(4, new java.sql.Date(checkOutTime));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> getBookingsByUsername(String username) {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.room_type, b.check_in_date, b.check_out_date, b.status " +
                "FROM bookings b JOIN users u ON b.user_id = u.id " +
                "WHERE u.username = ? ORDER BY b.check_in_date DESC";
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

    public boolean cancelBookingById(int bookingId) {
        String sql = "UPDATE bookings SET status = 'Canceled' WHERE booking_id = ? AND status = 'Confirmed'";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error canceling booking: " + e.getMessage());
            return false;
        }
    }


    // --- Room & Availability Management ---

    public List<Map<String, Object>> getAllRoomTypes() {
        List<Map<String, Object>> roomTypes = new ArrayList<>();
        String sql = "SELECT type_name, price_per_night, total_inventory, description, amenities FROM room_types ORDER BY price_per_night DESC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> room = new HashMap<>();
                room.put("type_name", rs.getString("type_name"));
                room.put("price_per_night", rs.getBigDecimal("price_per_night"));
                room.put("total_inventory", rs.getInt("total_inventory"));
                room.put("description", rs.getString("description"));
                room.put("amenities", rs.getString("amenities"));
                roomTypes.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all room types: " + e.getMessage());
        }
        return roomTypes;
    }

    public int getBookingCountForDateRange(String roomType, java.util.Date checkInDate, int nights) {
        long checkInTime = checkInDate.getTime();
        long checkOutTime = checkInTime + TimeUnit.DAYS.toMillis(nights);
        java.sql.Date sqlCheckOut = new java.sql.Date(checkOutTime);
        java.sql.Date sqlCheckIn = new java.sql.Date(checkInTime);
        String sql = "SELECT COUNT(*) FROM bookings WHERE room_type = ? AND " +
                "status = 'Confirmed' AND check_in_date < ? AND check_out_date > ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomType);
            pstmt.setDate(2, sqlCheckOut);
            pstmt.setDate(3, sqlCheckIn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting bookings: " + e.getMessage());
        }
        return 0;
    }

    public List<Object[]> getRoomFares() {
        List<Object[]> fares = new ArrayList<>();
        String sql = "SELECT type_name, price_per_night FROM room_types ORDER BY type_name";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fares.add(new Object[]{
                        rs.getString("type_name"),
                        rs.getBigDecimal("price_per_night")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching room fares: " + e.getMessage());
        }
        return fares;
    }

    public boolean updateRoomFare(String roomType, double newPrice) {
        String sql = "UPDATE room_types SET price_per_night = ? WHERE type_name = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, BigDecimal.valueOf(newPrice));
            pstmt.setString(2, roomType);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room fare: " + e.getMessage());
            return false;
        }
    }


    // --- Dashboard Analytics ---

    public int getUpcomingBookingsCount(String username) {
        String sql = "SELECT COUNT(*) FROM bookings b JOIN users u ON b.user_id = u.id " +
                "WHERE u.username = ? AND b.check_in_date >= CURRENT_DATE AND b.status = 'Confirmed'";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching upcoming bookings count: " + e.getMessage());
        }
        return 0;
    }

    public int getPastBookingsCount(String username) {
        String sql = "SELECT COUNT(*) FROM bookings b JOIN users u ON b.user_id = u.id " +
                "WHERE u.username = ? AND b.check_out_date < CURRENT_DATE";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching past bookings count: " + e.getMessage());
        }
        return 0;
    }

    public Map<String, Integer> getBookedRoomsCountForToday() {
        Map<String, Integer> bookedRooms = new HashMap<>();
        String sql = "SELECT room_type, COUNT(*) AS count FROM bookings " +
                "WHERE CURRENT_DATE >= check_in_date AND CURRENT_DATE < check_out_date " +
                "AND status = 'Confirmed' " +
                "GROUP BY room_type";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookedRooms.put(rs.getString("room_type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching today's booked rooms count: " + e.getMessage());
        }
        System.out.println("Booked rooms today map: " + bookedRooms);
        return bookedRooms;
    }


    public List<Object[]> getAllUpcomingBookings() {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, u.full_name, b.room_type, b.check_in_date, b.check_out_date " +
                "FROM bookings b JOIN users u ON b.user_id = u.id " +
                "WHERE b.check_in_date >= CURRENT_DATE AND b.status = 'Confirmed' " +
                "ORDER BY b.check_in_date ASC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("full_name"),
                        rs.getString("room_type"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all upcoming bookings: " + e.getMessage());
        }
        return bookings;
    }

    public Map<String, Object> getInvoiceDetails(int bookingId) {
        // This query has been completely rewritten to match your table structure.
        // It now correctly joins bookings -> users and bookings -> room_types.
        String sql = "SELECT u.username, rt.type_name, rt.price_per_night, b.check_in_date, b.check_out_date " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN room_types rt ON b.room_type = rt.type_name " +
                "WHERE b.booking_id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> details = new HashMap<>();
                details.put("guestName", rs.getString("username"));
                details.put("roomType", rs.getString("type_name"));
                // IMPORTANT: The column name is 'price_per_night', not 'price'
                details.put("price", rs.getDouble("price_per_night"));
                details.put("checkIn", rs.getDate("check_in_date"));
                details.put("checkOut", rs.getDate("check_out_date"));
                return details;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}