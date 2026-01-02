package com.fcms.models.users;

public abstract class UserAccount {

    protected String userID;
    protected String username;
    protected String name;
    protected String email;
    protected String password;
    protected String role;
    protected String managedBy;
    protected String createdAt;
    protected boolean approved;

    // ========= FULL CONSTRUCTOR (all fields) =========
    public UserAccount(String userID,
                       String username,
                       String name,
                       String email,
                       String password,
                       String role,
                       String managedBy,
                       boolean approved,
                       String createdAt) {

        this.userID = userID;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.managedBy = managedBy;
        this.approved = approved;
        this.createdAt = createdAt;
    }

    // ========= CONSTRUCTOR for new users (createdAt auto) =========
    public UserAccount(String userID,
                       String username,
                       String name,
                       String email,
                       String password,
                       String role,
                       String managedBy,
                       boolean approved) {

        this(userID, username, name, email, password, role, managedBy, approved, null);
    }

    // ========= GETTERS =========
    public String getUserID() { return userID; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getManagedBy() { return managedBy; }
    public boolean isApproved() { return approved; }
    public String getCreatedAt() { return createdAt; }

    // ========= SETTERS =========
    public void setUserID(String userID) { this.userID = userID; }
    public void setUsername(String username) { this.username = username; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setManagedBy(String managedBy) { this.managedBy = managedBy; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}