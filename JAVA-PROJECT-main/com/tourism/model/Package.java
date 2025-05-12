package com.tourism.model;

import java.math.BigDecimal;

public class Package {
    private int packageId;
    private String name;
    private String description;
    private String location;
    private int duration;
    private BigDecimal price; // Use BigDecimal for currency

    // Constructors
    public Package() {}

    public Package(int packageId, String name, String description, String location, int duration, BigDecimal price) {
        this.packageId = packageId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.duration = duration;
        this.price = price;
    }

    // Getters and Setters
    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

     @Override
    public String toString() { // For display
        return name + " - " + duration + " days (" + price + " Birr)";
    }
}