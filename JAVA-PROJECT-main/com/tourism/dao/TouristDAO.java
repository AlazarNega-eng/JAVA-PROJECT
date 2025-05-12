package com.tourism.dao;

import com.tourism.model.Tourist;
import com.tourism.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TouristDAO {

    // Add a new tourist
    public boolean addTourist(Tourist tourist) {
        String sql = "INSERT INTO tourist (name, email, phone, address, nationality) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tourist.getName());
            pstmt.setString(2, tourist.getEmail());
            pstmt.setString(3, tourist.getPhone());
            pstmt.setString(4, tourist.getAddress());
            pstmt.setString(5, tourist.getNationality());

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding tourist: " + e.getMessage());
            // Check for unique constraint violation (e.g., email)
             if (e.getSQLState().equals("23000")) { // Integrity constraint violation
                 System.err.println("Possible duplicate entry for email.");
             }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // Connection managed centrally
        }
        return success;
    }

    // Get a tourist by ID
    public Tourist getTouristById(int touristId) {
        Tourist tourist = null;
        String sql = "SELECT * FROM tourist WHERE tourist_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, touristId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                tourist = new Tourist();
                tourist.setTouristId(rs.getInt("tourist_id"));
                tourist.setName(rs.getString("name"));
                tourist.setEmail(rs.getString("email"));
                tourist.setPhone(rs.getString("phone"));
                tourist.setAddress(rs.getString("address"));
                tourist.setNationality(rs.getString("nationality"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tourist by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return tourist;
    }

    // Get all tourists
    public List<Tourist> getAllTourists() {
        List<Tourist> tourists = new ArrayList<>();
        String sql = "SELECT * FROM tourist ORDER BY name";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Tourist tourist = new Tourist();
                tourist.setTouristId(rs.getInt("tourist_id"));
                tourist.setName(rs.getString("name"));
                tourist.setEmail(rs.getString("email"));
                tourist.setPhone(rs.getString("phone"));
                tourist.setAddress(rs.getString("address"));
                tourist.setNationality(rs.getString("nationality"));
                tourists.add(tourist);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all tourists: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return tourists;
    }

    // Update an existing tourist
    public boolean updateTourist(Tourist tourist) {
        String sql = "UPDATE tourist SET name=?, email=?, phone=?, address=?, nationality=? WHERE tourist_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tourist.getName());
            pstmt.setString(2, tourist.getEmail());
            pstmt.setString(3, tourist.getPhone());
            pstmt.setString(4, tourist.getAddress());
            pstmt.setString(5, tourist.getNationality());
            pstmt.setInt(6, tourist.getTouristId()); // WHERE clause

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
             System.err.println("Error updating tourist: " + e.getMessage());
              if (e.getSQLState().equals("23000")) {
                 System.err.println("Possible duplicate entry for email during update.");
             }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    // Delete a tourist
    public boolean deleteTourist(int touristId) {
        // Note: Be careful! Deleting a tourist might cascade delete bookings if set up that way.
        // Consider logical deletion (e.g., setting an 'is_active' flag) instead of physical deletion in real apps.
        String sql = "DELETE FROM tourist WHERE tourist_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, touristId);

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting tourist: " + e.getMessage());
            // Check if deletion failed due to foreign key constraints (e.g., existing bookings)
             if (e.getSQLState().startsWith("23")) {
                 System.err.println("Cannot delete tourist because they have existing bookings or other related records.");
             }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }
}