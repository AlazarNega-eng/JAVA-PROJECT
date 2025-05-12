package com.tourism.model;

public class Tourist {
    private int touristId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String nationality;

    // Constructors (default and parameterized)
    public Tourist() {}

    public Tourist(int touristId, String name, String email, String phone, String address, String nationality) {
        this.touristId = touristId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.nationality = nationality;
    }

    // Getters and Setters for all fields
    public int getTouristId() { return touristId; }
    public void setTouristId(int touristId) { this.touristId = touristId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    @Override
    public String toString() { // Useful for displaying in ComboBoxes, etc.
        return name + " (" + touristId + ")";
    }
}