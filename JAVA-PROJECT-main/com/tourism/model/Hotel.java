package com.tourism.model;

import java.math.BigDecimal;

public class Hotel {
    private int hotelId;
    private String name;
    private String location;
    private String contact;
    private BigDecimal rating; // Use BigDecimal for potential fractional ratings

    // Constructors
    public Hotel() {}

    public Hotel(int hotelId, String name, String location, String contact, BigDecimal rating) {
        this.hotelId = hotelId;
        this.name = name;
        this.location = location;
        this.contact = contact;
        this.rating = rating;
    }

    // Getters and Setters
    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    @Override
    public String toString() { // For display
        return name + " - " + location + (rating != null ? " (" + rating + "*)" : "");
    }
}