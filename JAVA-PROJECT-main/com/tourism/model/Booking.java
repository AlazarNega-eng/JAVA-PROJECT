package com.tourism.model;

import java.math.BigDecimal;
import java.sql.Timestamp; // Use java.sql.Timestamp for easier JDBC mapping

public class Booking {
    private int bookingId;
    private int touristId;
    private int packageId;
    private Integer guideId; // Use Integer wrapper class to allow null
    private Timestamp bookingDate;
    private BigDecimal totalCost;
    private String status;

    // Optional: Fields to hold related objects (loaded separately if needed)
    private Tourist tourist;
    private Package tourPackage; // Renamed to avoid conflict with java.lang.Package
    private Guide guide;

    // Constructors
    public Booking() {}

    public Booking(int bookingId, int touristId, int packageId, Integer guideId, Timestamp bookingDate, BigDecimal totalCost, String status) {
        this.bookingId = bookingId;
        this.touristId = touristId;
        this.packageId = packageId;
        this.guideId = guideId;
        this.bookingDate = bookingDate;
        this.totalCost = totalCost;
        this.status = status;
    }

    // Getters and Setters for IDs and direct attributes
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getTouristId() { return touristId; }
    public void setTouristId(int touristId) { this.touristId = touristId; }
    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }
    public Integer getGuideId() { return guideId; } // Allows null
    public void setGuideId(Integer guideId) { this.guideId = guideId; } // Allows null
    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { this.bookingDate = bookingDate; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Getters and Setters for related objects (optional, set externally)
    public Tourist getTourist() { return tourist; }
    public void setTourist(Tourist tourist) { this.tourist = tourist; }
    public Package getTourPackage() { return tourPackage; }
    public void setTourPackage(Package tourPackage) { this.tourPackage = tourPackage; }
    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }


    @Override
    public String toString() {
        return "Booking ID: " + bookingId + ", Tourist ID: " + touristId + ", Package ID: " + packageId + ", Status: " + status;
    }
}