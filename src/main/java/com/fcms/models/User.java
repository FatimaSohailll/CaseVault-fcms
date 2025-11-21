package com.fcms.models;

public class User {
    private String name;
    private String email;
    private String role;
    private String department;
    private String status;

    public User(String name, String email, String role, String department, String status) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
        this.status = status;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
