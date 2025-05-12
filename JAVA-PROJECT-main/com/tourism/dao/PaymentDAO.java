package com.tourism.dao;

import com.tourism.model.Payment;
import com.tourism.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // Add a new payment
    public boolean addPayment(Payment payment) {
        // booking_id is UNIQUE, so only one payment per booking allowed by this schema
        String sql = "INSERT INTO payment (booking_id, amount, payment_date, payment_method, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, payment.getBookingId());
            pstmt.setBigDecimal(2, payment.getAmount());
             Timestamp paymentTimestamp = payment.getPaymentDate() != null ? payment.getPaymentDate() : new Timestamp(System.currentTimeMillis());
             pstmt.setTimestamp(3, paymentTimestamp);
            pstmt.setString(4, payment.getPaymentMethod());
            pstmt.setString(5, payment.getStatus() != null ? payment.getStatus() : "Completed"); // Default status

            int rowsAffected = pstmt.executeUpdate();

             if (rowsAffected > 0) {
                 // Get the generated payment ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setPaymentId(generatedKeys.getInt(1));
                        success = true;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
             if (e.getSQLState().equals("23000")) { // Integrity constraint violation
                 System.err.println("Possible duplicate payment for this booking ID, or Booking ID does not exist.");
             }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    // Get a payment by ID
    public Payment getPaymentById(int paymentId) {
        Payment payment = null;
        String sql = "SELECT * FROM payment WHERE payment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, paymentId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                payment = mapResultSetToPayment(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payment by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return payment;
    }

     // Get a payment by Booking ID (since it's unique)
    public Payment getPaymentByBookingId(int bookingId) {
        Payment payment = null;
        String sql = "SELECT * FROM payment WHERE booking_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                payment = mapResultSetToPayment(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payment by Booking ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return payment;
    }


    // Get all payments (less common, but possible)
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment ORDER BY payment_date DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                 payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all payments: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return payments;
    }

    // Update an existing payment
    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE payment SET booking_id=?, amount=?, payment_date=?, payment_method=?, status=? WHERE payment_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, payment.getBookingId());
            pstmt.setBigDecimal(2, payment.getAmount());
            pstmt.setTimestamp(3, payment.getPaymentDate());
            pstmt.setString(4, payment.getPaymentMethod());
            pstmt.setString(5, payment.getStatus());
            pstmt.setInt(6, payment.getPaymentId()); // WHERE clause

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
             if (e.getSQLState().equals("23000")) { // Integrity constraint violation
                 System.err.println("Possible duplicate payment for this booking ID during update, or Booking ID does not exist.");
             }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    // Delete a payment by Payment ID
    public boolean deletePayment(int paymentId) {
        String sql = "DELETE FROM payment WHERE payment_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, paymentId);

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

     // Delete a payment by Booking ID (useful if CASCADE DELETE isn't set on Booking)
     // Note: Use carefully, ensure transaction safety if needed alongside booking deletion.
    public boolean deletePaymentByBookingId(int bookingId) {
        String sql = "DELETE FROM payment WHERE booking_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection(); // Get connection
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0; // May affect 0 rows if no payment existed

        } catch (SQLException e) {
            System.err.println("Error deleting payment by booking ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
             // Don't close connection here if it's managed outside
        }
        return success;
    }


     // Helper method to map ResultSet row to Payment object
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setBookingId(rs.getInt("booking_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setStatus(rs.getString("status"));
        return payment;
    }
}