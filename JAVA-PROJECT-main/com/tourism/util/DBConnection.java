package com.tourism.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // --- Database Configuration ---
    // Replace with your database details if they are different
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tourism_db";
    private static final String DB_USER = "root";       // Default XAMPP user
    private static final String DB_PASSWORD = "3205";   // Default XAMPP password (empty)
    // -----------------------------

    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DBConnection() {}

    // Method to get the database connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the MySQL JDBC driver (optional for newer JDBC versions)
                // Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish the connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection successful!"); // For debugging

            } catch (SQLException e) {
                System.err.println("Database Connection Error: " + e.getMessage());
                e.printStackTrace(); // Print detailed error
                throw e; // Re-throw the exception to signal failure
            }
            // catch (ClassNotFoundException e) {
            //     System.err.println("MySQL JDBC Driver not found!");
            //     e.printStackTrace();
            //     throw new SQLException("JDBC Driver not found", e);
            // }
        }
        return connection;
    }

    // Method to close the connection (optional, can be called on app shutdown)
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Reset connection variable
                System.out.println("Database connection closed."); // For debugging
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Main method for simple connection testing
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Test connection successful!");
                DBConnection.closeConnection();
            } else {
                System.out.println("Test connection failed.");
            }
        } catch (SQLException e) {
            System.err.println("Test connection failed with exception.");
        }
    }
}