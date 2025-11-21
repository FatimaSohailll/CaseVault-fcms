package com.fcms.models;

import java.time.LocalDate;

public class Case {
    private String id;
    private String title;
    private String type;
    private String officer;
    private String location;
    private LocalDate date;
    private String status;

    public Case() {}

    public Case(String id, String title, String type, String officer, String location, LocalDate date, String status) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.officer = officer;
        this.location = location;
        this.date = date;
        this.status = status;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOfficer() { return officer; }
    public void setOfficer(String officer) { this.officer = officer; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}