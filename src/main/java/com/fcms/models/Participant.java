package com.fcms.models;

import javafx.beans.property.SimpleStringProperty;

public class Participant {
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty role;
    private final SimpleStringProperty contact;
    private final SimpleStringProperty idType;
    private final SimpleStringProperty idNumber;

    public Participant(String id, String name, String role, String contact, String idType, String idNumber) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.contact = new SimpleStringProperty(contact);
        this.idType = new SimpleStringProperty(idType);
        this.idNumber = new SimpleStringProperty(idNumber);
    }

    // Getters and property methods
    public String getId() { return id.get(); }
    public SimpleStringProperty idProperty() { return id; }
    public void setId(String id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public SimpleStringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getRole() { return role.get(); }
    public SimpleStringProperty roleProperty() { return role; }
    public void setRole(String role) { this.role.set(role); }

    public String getContact() { return contact.get(); }
    public SimpleStringProperty contactProperty() { return contact; }
    public void setContact(String contact) { this.contact.set(contact); }

    public String getIdType() { return idType.get(); }
    public SimpleStringProperty idTypeProperty() { return idType; }
    public void setIdType(String idType) { this.idType.set(idType); }

    public String getIdNumber() { return idNumber.get(); }
    public SimpleStringProperty idNumberProperty() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber.set(idNumber); }
}