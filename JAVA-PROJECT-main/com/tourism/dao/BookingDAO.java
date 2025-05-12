package com.tourism.dao;

import com.tourism.model.Booking;
// Optional: Import other models if you want to return enriched Booking objects
// import com.tourism.model.Tourist;
// import com.tourism.model.Package;
// import com.tourism.model.Guide;
import com.tourism.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // Add a new booking
    // Assumes totalCost is calculated *before* calling this method
    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO booking (tourist_id, package_id, guide_id, total_cost, status, booking_date) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // To get the auto-generated booking_id

            pstmt.setInt(1, booking.getTouristId());
            pstmt.setInt(2, booking.getPackageId());

            // Handle nullable guide_id
            if (booking.getGuideId() != null) {
                pstmt.setInt(3, booking.getGuideId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setBigDecimal(4, booking.getTotalCost());
            pstmt.setString(5, booking.getStatus() != null ? booking.getStatus() : "Pending"); // Default status

            // Handle booking date (use current time if not provided)
             Timestamp bookingTimestamp = booking.getBookingDate() != null ? booking.getBookingDate() : new Timestamp(System.currentTimeMillis());
             pstmt.setTimestamp(6, bookingTimestamp);


            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                 // Get the generated booking ID and set it in the booking object
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setBookingId(generatedKeys.getInt(1));
                        success = true;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding booking: " + e.getMessage());
             if (e.getSQLState().startsWith("23")) { // FK violation etc.
                 System.err.println("Ensure Tourist ID and Package ID exist.");
             }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    // Get a booking by ID
    public Booking getBookingById(int bookingId) {
        Booking booking = null;
        String sql = "SELECT * FROM booking WHERE booking_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                booking = mapResultSetToBooking(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booking by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return booking;
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        // Optionally join with other tables to get names directly if needed frequently
        // String sql = "SELECT b.*, t.name as tourist_name, p.name as package_name FROM booking b " +
        //              "JOIN tourist t ON b.tourist_id = t.tourist_id " +
        //              "JOIN package p ON b.package_id = p.package_id ORDER BY b.booking_date DESC";
        String sql = "SELECT * FROM booking ORDER BY booking_date DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return bookings;
    }

     // Get bookings for a specific tourist
    public List<Booking> getBookingsByTouristId(int touristId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE tourist_id = ? ORDER BY booking_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
             pstmt.setInt(1, touristId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings by tourist ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return bookings;
    }


    // Update an existing booking
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE booking SET tourist_id=?, package_id=?, guide_id=?, total_cost=?, status=?, booking_date=? WHERE booking_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, booking.getTouristId());
            pstmt.setInt(2, booking.getPackageId());
             if (booking.getGuideId() != null) {
                pstmt.setInt(3, booking.getGuideId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setBigDecimal(4, booking.getTotalCost());
            pstmt.setString(5, booking.getStatus());
            pstmt.setTimestamp(6, booking.getBookingDate());
            pstmt.setInt(7, booking.getBookingId()); // WHERE clause

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            if (e.getSQLState().startsWith("23")) { // FK violation etc.
                 System.err.println("Ensure Tourist ID and Package ID exist during update.");
             }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    // Delete a booking
    public boolean deleteBooking(int bookingId) {
        // Deleting a booking might cascade delete payments if set up that way.
        String sql = "DELETE FROM booking WHERE booking_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            // Optional: Delete related payments first if CASCADE is not set
            // new PaymentDAO().deletePaymentByBookingId(conn, bookingId); // Assumes such a method exists

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
             if (e.getSQLState().startsWith("23")) {
                 System.err.println("Cannot delete booking due to related records (e.g., payments). Delete payments first.");
             }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    // Helper method to map ResultSet row to Booking object
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setTouristId(rs.getInt("tourist_id"));
        booking.setPackageId(rs.getInt("package_id"));
        // Handle potential null guide_id from DB
        int guideIdInt = rs.getInt("guide_id");
        booking.setGuideId(rs.wasNull() ? null : guideIdInt); // Store as Integer wrapper
        booking.setBookingDate(rs.getTimestamp("booking_date"));
        booking.setTotalCost(rs.getBigDecimal("total_cost"));
        booking.setStatus(rs.getString("status"));
        return booking;
    }
}