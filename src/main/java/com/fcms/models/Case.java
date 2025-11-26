package com.fcms.models;

import java.time.LocalDate;

public class Case {
    private String id;
    private String title;
    private String type;
    private String assignedOfficer;
    private String location;
    private LocalDate dateRegistered;
    private String status;

    // Extended fields
    private String description;
    private String priority;

    public Case() {}

    public Case(String id, String title, String type, String assignedOfficer,
                String location, LocalDate dateRegistered, String status,
                String description, String priority) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.assignedOfficer = assignedOfficer;
        this.location = location;
        this.dateRegistered = dateRegistered;
        this.status = status;
        this.description = description;
        this.priority = priority;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAssignedOfficer() { return assignedOfficer; }
    public void setAssignedOfficer(String assignedOfficer) { this.assignedOfficer = assignedOfficer; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getDateRegistered() { return dateRegistered; }
    public void setDateRegistered(LocalDate dateRegistered) { this.dateRegistered = dateRegistered; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }


    public boolean isSubmittedToCourt() {
        return "submitted".equalsIgnoreCase(status);
    }

    public boolean hasVerdict() {
        return "closed".equalsIgnoreCase(status);
    }

    public String getOfficerName() {
        return assignedOfficer;
    }

    public String getCreatedAt() {
        return dateRegistered != null ? dateRegistered.toString() : "";
    }

}