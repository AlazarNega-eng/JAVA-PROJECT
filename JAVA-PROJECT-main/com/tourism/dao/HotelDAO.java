package com.tourism.dao;

import com.tourism.model.Hotel;
import com.tourism.util.DBConnection;

import java.math.BigDecimal; // Ensure BigDecimal is imported
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class HotelDAO {

    /**
     * Adds a new hotel to the database.
     * @param hotel The Hotel object containing the data for the new hotel.
     * @return true if the hotel was added successfully, false otherwise.
     */
    public boolean addHotel(Hotel hotel) {
        String sql = "INSERT INTO hotel (name, location, contact, rating) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hotel.getName());
            pstmt.setString(2, hotel.getLocation());
            pstmt.setString(3, hotel.getContact());
            // Handle potential null rating
            if (hotel.getRating() != null) {
                pstmt.setBigDecimal(4, hotel.getRating());
            } else {
                pstmt.setNull(4, Types.DECIMAL);
            }

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding hotel: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed debugging
        } finally {
            // Close PreparedStatement
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // Connection is managed centrally by DBConnection, so don't close it here
        }
        return success;
    }

    /**
     * Retrieves a specific hotel from the database by its ID.
     * @param hotelId The ID of the hotel to retrieve.
     * @return The Hotel object if found, or null if not found or an error occurs.
     */
    public Hotel getHotelById(int hotelId) {
        Hotel hotel = null;
        String sql = "SELECT * FROM hotel WHERE hotel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hotelId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Use the helper method to map the ResultSet row to a Hotel object
                hotel = mapResultSetToHotel(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotel by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources in reverse order
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // Connection managed centrally
        }
        return hotel;
    }

    /**
     * Retrieves a list of all hotels from the database, ordered by name.
     * THIS METHOD SHOULD BE FULLY IMPLEMENTED AND WORKING.
     * @return A List of Hotel objects, or an empty list if no hotels are found or an error occurs.
     */
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotel ORDER BY name"; // SQL query to get all hotels
        Connection conn = null;
        Statement stmt = null; // Use Statement as no parameters are needed here
        ResultSet rs = null;

        // ---- START OF IMPLEMENTATION ----
        try {
            conn = DBConnection.getConnection(); // Get database connection
            stmt = conn.createStatement();       // Create a statement object
            rs = stmt.executeQuery(sql);         // Execute the SQL query

            // Iterate through the results
            while (rs.next()) {
                // Map the current row data to a Hotel object using the helper method
                Hotel hotel = mapResultSetToHotel(rs);
                // Add the created Hotel object to the list
                hotels.add(hotel);
            }
        } catch (SQLException e) {
            // Handle any potential database errors
            System.err.println("Error fetching all hotels: " + e.getMessage());
            e.printStackTrace(); // Print detailed error information
        } finally {
            // Ensure database resources (ResultSet and Statement) are closed
            // Close resources in the reverse order they were opened
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
             // Connection is managed centrally, so don't close 'conn' here
        }
        // ---- END OF IMPLEMENTATION ----

        // Return the list of hotels (will be empty if none found or an error occurred)
        return hotels;
    }

    /**
     * Updates the details of an existing hotel in the database.
     * @param hotel The Hotel object containing the updated data (must include the correct hotel_id).
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateHotel(Hotel hotel) {
        String sql = "UPDATE hotel SET name=?, location=?, contact=?, rating=? WHERE hotel_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hotel.getName());
            pstmt.setString(2, hotel.getLocation());
            pstmt.setString(3, hotel.getContact());
             if (hotel.getRating() != null) {
                pstmt.setBigDecimal(4, hotel.getRating());
            } else {
                pstmt.setNull(4, Types.DECIMAL);
            }
            // Set the ID for the WHERE clause
            pstmt.setInt(5, hotel.getHotelId());

            int rowsAffected = pstmt.executeUpdate();
            // Update is successful if at least one row was affected
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating hotel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
             // Connection managed centrally
        }
        return success;
    }

    /**
     * Deletes a hotel from the database by its ID.
     * It first attempts to unlink the hotel from all associated packages.
     * @param hotelId The ID of the hotel to delete.
     * @return true if the deletion (including unlinking) was successful, false otherwise.
     */
    public boolean deleteHotel(int hotelId) {
        // Attempt to unlink the hotel from packages first to avoid foreign key constraints
        boolean unlinked = unlinkHotelFromAllPackages(hotelId);
        if (!unlinked) {
             System.err.println("Failed to unlink hotel " + hotelId + " from packages. Deletion aborted.");
            return false; // Abort deletion if unlinking failed
        }

        String sql = "DELETE FROM hotel WHERE hotel_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hotelId);

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting hotel (after attempting unlink): " + e.getMessage());
            e.printStackTrace();
             // If an error still occurs, it might be due to other unexpected dependencies
             if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                 System.err.println("Cannot delete hotel due to unexpected remaining dependencies.");
             }
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
             // Connection managed centrally
        }
        return success;
    }

    // --- Methods for managing package_hotel relationship ---

    /**
     * Links a specific hotel to a specific package in the package_hotel table.
     * @param packageId The ID of the package.
     * @param hotelId   The ID of the hotel.
     * @return true if the link was successfully created, false otherwise.
     */
    public boolean linkHotelToPackage(int packageId, int hotelId) {
        String sql = "INSERT INTO package_hotel (package_id, hotel_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, packageId);
            pstmt.setInt(2, hotelId);

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23000")) { // Check for specific SQLState for constraint violation
                System.err.println("Error linking hotel to package: This link likely already exists or invalid IDs provided.");
            } else {
                System.err.println("Error linking hotel to package: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    /**
     * Unlinks a specific hotel from a specific package in the package_hotel table.
     * @param packageId The ID of the package.
     * @param hotelId   The ID of the hotel.
     * @return true if the link was successfully removed (or didn't exist), false on error.
     */
    public boolean unlinkHotelFromPackage(int packageId, int hotelId) {
        String sql = "DELETE FROM package_hotel WHERE package_id = ? AND hotel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, packageId);
            pstmt.setInt(2, hotelId);

            pstmt.executeUpdate(); // Execute deletion
            success = true; // Assume success if no exception occurred (even if 0 rows affected)

        } catch (SQLException e) {
            System.err.println("Error unlinking hotel from package: " + e.getMessage());
            e.printStackTrace();
            success = false; // Explicitly set to false on error
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    /**
     * Unlinks a specific hotel from ALL packages. This is a private helper method
     * typically called before deleting a hotel to prevent foreign key constraint violations.
     * @param hotelId The ID of the hotel to unlink from all packages.
     * @return true if successful or no links existed, false on error.
     */
    private boolean unlinkHotelFromAllPackages(int hotelId) {
        String sql = "DELETE FROM package_hotel WHERE hotel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hotelId);

            pstmt.executeUpdate(); // Execute delete, number of rows affected is not critical here
            success = true; // Assume success if no exception is thrown

        } catch (SQLException e) {
            System.err.println("Error unlinking hotel " + hotelId + " from all packages: " + e.getMessage());
            e.printStackTrace();
            success = false; // Mark as failed if an error occurs
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    /**
     * Retrieves a list of all hotels associated with a specific package ID.
     * @param packageId The ID of the package.
     * @return A List of Hotel objects linked to the package, or an empty list if none found or an error occurs.
     */
    public List<Hotel> getHotelsByPackageId(int packageId) {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT h.* FROM hotel h " +
                     "JOIN package_hotel ph ON h.hotel_id = ph.hotel_id " +
                     "WHERE ph.package_id = ? ORDER BY h.name";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, packageId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Hotel hotel = mapResultSetToHotel(rs); // Use helper
                hotels.add(hotel);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotels by package ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return hotels;
    }

    /**
     * Helper method to map a ResultSet row to a Hotel object. Reduces code duplication.
     * @param rs The ResultSet cursor positioned at the row to map.
     * @return A populated Hotel object based on the current ResultSet row.
     * @throws SQLException If a database access error occurs when reading from the ResultSet.
     */
    private Hotel mapResultSetToHotel(ResultSet rs) throws SQLException {
        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getInt("hotel_id"));
        hotel.setName(rs.getString("name"));
        hotel.setLocation(rs.getString("location"));
        hotel.setContact(rs.getString("contact"));
        // Use getBigDecimal for rating, as it correctly handles SQL NULL by returning Java null
        hotel.setRating(rs.getBigDecimal("rating"));
        return hotel;
    }

} // End of HotelDAO class