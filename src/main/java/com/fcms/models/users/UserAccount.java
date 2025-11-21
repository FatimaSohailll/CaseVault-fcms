package com.fcms.models.users;

public class UserAccount {

    private String userID;
    private String username;
    private String name;
    private String email;
    private String password;   // NEW - full support
    private String role;
    private String managedBy;  // used as "status"
    private String createdAt;

    // ========= FULL CONSTRUCTOR (8 FIELDS) =========
    public UserAccount(String userID,
                       String username,
                       String name,
                       String email,
                       String password,
                       String role,
                       String managedBy,
                       String createdAt) {

        this.userID = userID;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.managedBy = managedBy;
        this.createdAt = createdAt;
    }

    // ========= SELECT CONSTRUCTOR (no password returned from DB) =========
    public UserAccount(String userID,
                       String username,
                       String name,
                       String email,
                       String role,
                       String managedBy,
                       String createdAt) {

        this(userID, username, name, email, null, role, managedBy, createdAt);
    }

    // ========= GETTERS =========
    public String getUserID() { return userID; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getManagedBy() { return managedBy; }
    public String getCreatedAt() { return createdAt; }

    // ========= SETTERS =========
    public void setUserID(String userID) { this.userID = userID; }
    public void setUsername(String username) { this.username = username; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setManagedBy(String managedBy) { this.managedBy = managedBy; }
}
