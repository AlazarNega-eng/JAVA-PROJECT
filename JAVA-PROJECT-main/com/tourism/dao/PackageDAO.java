package com.tourism.dao;

import com.tourism.model.Package;
import com.tourism.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackageDAO {

    // Method to get all packages from the database
    public List<Package> getAllPackages() {
        List<Package> packages = new ArrayList<>();
        String sql = "SELECT * FROM package ORDER BY name"; // Order alphabetically
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Package pkg = new Package();
                pkg.setPackageId(rs.getInt("package_id"));
                pkg.setName(rs.getString("name"));
                pkg.setDescription(rs.getString("description"));
                pkg.setLocation(rs.getString("location"));
                pkg.setDuration(rs.getInt("duration"));
                pkg.setPrice(rs.getBigDecimal("price"));
                packages.add(pkg);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching packages: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources in reverse order of creation
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // Don't close the connection here if it's managed centrally (like in DBConnection)
            // If you need to close it after every operation:
            // try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return packages;
    }

    // Method to get a single package by ID
    public Package getPackageById(int packageId) {
        Package pkg = null;
        String sql = "SELECT * FROM package WHERE package_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, packageId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                pkg = new Package();
                pkg.setPackageId(rs.getInt("package_id"));
                pkg.setName(rs.getString("name"));
                pkg.setDescription(rs.getString("description"));
                pkg.setLocation(rs.getString("location"));
                pkg.setDuration(rs.getInt("duration"));
                pkg.setPrice(rs.getBigDecimal("price"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching package by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return pkg;
    }

    // --- Add methods for adding, updating, deleting packages ---
    // Example: Add Package (requires Package object without ID)
    public boolean addPackage(Package pkg) {
        String sql = "INSERT INTO package (name, description, location, duration, price) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pkg.getName());
            pstmt.setString(2, pkg.getDescription());
            pstmt.setString(3, pkg.getLocation());
            pstmt.setInt(4, pkg.getDuration());
            pstmt.setBigDecimal(5, pkg.getPrice());

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding package: " + e.getMessage());
            e.printStackTrace();
        } finally {
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

     // Example: Update Package
    public boolean updatePackage(Package pkg) {
        String sql = "UPDATE package SET name=?, description=?, location=?, duration=?, price=? WHERE package_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pkg.getName());
            pstmt.setString(2, pkg.getDescription());
            pstmt.setString(3, pkg.getLocation());
            pstmt.setInt(4, pkg.getDuration());
            pstmt.setBigDecimal(5, pkg.getPrice());
            pstmt.setInt(6, pkg.getPackageId()); // Set the ID for the WHERE clause

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating package: " + e.getMessage());
            e.printStackTrace();
        } finally {
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    // Example: Delete Package
    public boolean deletePackage(int packageId) {
        String sql = "DELETE FROM package WHERE package_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, packageId);

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            // Handle potential foreign key constraint errors if bookings exist for this package
             if (e.getSQLState().startsWith("23")) { // SQLSTATE for integrity constraint violation
                 System.err.println("Error deleting package: Cannot delete package because it is referenced in bookings.");
             } else {
                 System.err.println("Error deleting package: " + e.getMessage());
                 e.printStackTrace();
             }
        } finally {
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

}