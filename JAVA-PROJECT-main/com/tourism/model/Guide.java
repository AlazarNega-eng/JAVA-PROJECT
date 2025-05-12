package com.tourism.model;

public class Guide {
    private int guideId;
    private String name;
    private String contact;
    private String language;
    private String experience;

    // Constructors
    public Guide() {}

    public Guide(int guideId, String name, String contact, String language, String experience) {
        this.guideId = guideId;
        this.name = name;
        this.contact = contact;
        this.language = language;
        this.experience = experience;
    }

    // Getters and Setters
    public int getGuideId() { return guideId; }
    public void setGuideId(int guideId) { this.guideId = guideId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    @Override
    public String toString() { // For display in ComboBoxes etc.
        return name + " (" + language + ")";
    }
}