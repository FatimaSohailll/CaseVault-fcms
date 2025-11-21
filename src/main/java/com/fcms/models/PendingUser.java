package com.fcms.models;

public class PendingUser {
    private String name;
    private String role;
    private String email;
    private String department;
    private String reason;
    private String appliedDate;

    public PendingUser(String name, String role, String email, String department, String reason, String appliedDate) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.department = department;
        this.reason = reason;
        this.appliedDate = appliedDate;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getReason() { return reason; }
    public String getAppliedDate() { return appliedDate; }
}
