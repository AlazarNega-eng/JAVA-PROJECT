package com.tourism.dao;

import com.tourism.model.Guide;
import com.tourism.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuideDAO {

    // Add a new guide
    public boolean addGuide(Guide guide) {
        String sql = "INSERT INTO guide (name, contact, language, experience) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, guide.getName());
            pstmt.setString(2, guide.getContact());
            pstmt.setString(3, guide.getLanguage());
            pstmt.setString(4, guide.getExperience());

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding guide: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    // Get a guide by ID
    public Guide getGuideById(int guideId) {
        Guide guide = null;
        String sql = "SELECT * FROM guide WHERE guide_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, guideId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                guide = new Guide();
                guide.setGuideId(rs.getInt("guide_id"));
                guide.setName(rs.getString("name"));
                guide.setContact(rs.getString("contact"));
                guide.setLanguage(rs.getString("language"));
                guide.setExperience(rs.getString("experience"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching guide by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return guide;
    }

    // Get all guides
    public List<Guide> getAllGuides() {
        List<Guide> guides = new ArrayList<>();
        String sql = "SELECT * FROM guide ORDER BY name";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Guide guide = new Guide();
                guide.setGuideId(rs.getInt("guide_id"));
                guide.setName(rs.getString("name"));
                guide.setContact(rs.getString("contact"));
                guide.setLanguage(rs.getString("language"));
                guide.setExperience(rs.getString("experience"));
                guides.add(guide);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all guides: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return guides;
    }

    // Update an existing guide
    public boolean updateGuide(Guide guide) {
        String sql = "UPDATE guide SET name=?, contact=?, language=?, experience=? WHERE guide_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, guide.getName());
            pstmt.setString(2, guide.getContact());
            pstmt.setString(3, guide.getLanguage());
            pstmt.setString(4, guide.getExperience());
            pstmt.setInt(5, guide.getGuideId()); // WHERE clause

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating guide: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    // Delete a guide
    public boolean deleteGuide(int guideId) {
        // Check if guide is assigned to bookings first if FK constraint is RESTRICT or handle SET NULL
        String sql = "DELETE FROM guide WHERE guide_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, guideId);

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting guide: " + e.getMessage());
             if (e.getSQLState().startsWith("23")) {
                 System.err.println("Cannot delete guide because they are assigned to existing bookings (or other dependencies exist).");
             }
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }
}