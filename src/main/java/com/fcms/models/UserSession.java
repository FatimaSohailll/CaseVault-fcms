package com.fcms.models;

public class UserSession {
    private static UserSession instance;
    private String userID;
    private String role;
    private String username;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(String userID, String role) {
        this.userID = userID;
        this.role = role;
    }

    public void setCurrentUser(String userID, String role, String username) {
        this.userID = userID;
        this.role = role;
        this.username = username;
    }

    public void clearSession() {
        this.userID = null;
        this.role = null;
        this.username = null;
    }

    public String getUserID() { return userID; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
    public boolean isLoggedIn() { return userID != null; }

    public boolean isPoliceOfficer() { return "Police Officer".equals(role); }
    public boolean isForensicExpert() { return "Forensic Expert".equals(role); }
    public boolean isCourtOfficial() { return "Court Official".equals(role); }
}