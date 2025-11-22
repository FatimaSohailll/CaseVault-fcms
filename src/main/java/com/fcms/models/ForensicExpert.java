package com.fcms.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ForensicExpert {
    private final StringProperty expertId;
    private final StringProperty name;
    private final StringProperty labName;
    private final StringProperty email;
    private final StringProperty specialization;

    // Constructor for database results
    public ForensicExpert(String expertId, String name, String labName, String email) {
        this.expertId = new SimpleStringProperty(expertId);
        this.name = new SimpleStringProperty(name);
        this.labName = new SimpleStringProperty(labName != null ? labName : "Forensic Lab");
        this.email = new SimpleStringProperty(email);
        this.specialization = new SimpleStringProperty("Forensic Expert"); // Default based on role
    }

    // Alternative constructor with specialization
    public ForensicExpert(String expertId, String name, String labName, String email, String specialization) {
        this.expertId = new SimpleStringProperty(expertId);
        this.name = new SimpleStringProperty(name);
        this.labName = new SimpleStringProperty(labName != null ? labName : "Forensic Lab");
        this.email = new SimpleStringProperty(email);
        this.specialization = new SimpleStringProperty(specialization);
    }

    // Getters
    public String getExpertId() { return expertId.get(); }
    public String getName() { return name.get(); }
    public String getLabName() { return labName.get(); }
    public String getEmail() { return email.get(); }
    public String getSpecialization() { return specialization.get(); }

    // Property getters (for JavaFX binding)
    public StringProperty expertIdProperty() { return expertId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty labNameProperty() { return labName; }
    public StringProperty emailProperty() { return email; }
    public StringProperty specializationProperty() { return specialization; }

    // Setters
    public void setExpertId(String expertId) { this.expertId.set(expertId); }
    public void setName(String name) { this.name.set(name); }
    public void setLabName(String labName) { this.labName.set(labName); }
    public void setEmail(String email) { this.email.set(email); }
    public void setSpecialization(String specialization) { this.specialization.set(specialization); }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", name.get(), labName.get(), specialization.get());
    }

    // Helper method to get display name for UI
    public String getDisplayName() {
        return name.get() + " - " + labName.get();
    }

    // Helper method to get contact info
    public String getContactInfo() {
        return email.get();
    }

    // Helper method to check if expert is available (you can extend this with actual availability logic)
    public boolean isAvailable() {
        // Add your availability logic here
        // For now, assume all experts are available
        return true;
    }
}