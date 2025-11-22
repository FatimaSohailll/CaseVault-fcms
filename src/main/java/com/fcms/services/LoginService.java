package com.fcms.services;

import com.fcms.repositories.AuthRepository;
import java.sql.SQLException;
import com.fcms.models.UserAccount;

public class LoginService {
    private final AuthRepository userRepository;

    public LoginService() {
        this.userRepository = new AuthRepository();
    }

    public LoginResult authenticate(String username, String password) {
        try {
            // Validate inputs
            if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
                return new LoginResult(false, "Please enter both Username and Password", null, null);
            }

            // Check if user exists and credentials match
            UserAccount user = userRepository.getUserCredentials(username);

            if (user == null) {
                return new LoginResult(false, "Invalid Username or Password", null, null);
            }

            if (!user.isApproved()) {
                return new LoginResult(false, "Your account is pending administrator approval", null, null);
            }

            // In production, you should hash the password and compare hashes
            if (!user.getPassword().equals(password)) {
                return new LoginResult(false, "Invalid Username or Password", null, null);
            }

            return new LoginResult(true, "Login successful", user.getUserID(), user.getRole());

        } catch (SQLException e) {
            return new LoginResult(false, "Database error: " + e.getMessage(), null, null);
        }
    }

    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final String userID;
        private final String role;

        public LoginResult(boolean success, String message, String userID, String role) {
            this.success = success;
            this.message = message;
            this.userID = userID;
            this.role = role;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUserID() { return userID; }
        public String getRole() { return role; }
    }
}