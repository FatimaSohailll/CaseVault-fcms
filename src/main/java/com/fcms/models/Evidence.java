package com.fcms.models;

public class Evidence {
    private String id;
    private String description;
    private String type;
    private boolean selected;
    private String collectionDateTime;
    private String location;
    private String fileName;
    private String caseId;

    public Evidence() {}

    public Evidence(String id, String description, String type) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.selected = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public String getCollectionDateTime() { return collectionDateTime; }
    public void setCollectionDateTime(String date) { this.collectionDateTime = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}