package com.fcms.models;

public class UserAccount {
    private final String userID;
    private final String username;
    private final String password;
    private final String role;
    private final boolean approved;

    public UserAccount(String userID, String username, String password, String role, boolean approved) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.approved = approved;
    }

    public String getUserID() { return userID; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isApproved() { return approved; }
}