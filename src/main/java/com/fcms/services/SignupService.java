package com.fcms.services;

import com.fcms.repositories.AuthRepository;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupService {
    private final AuthRepository userRepository;

    public SignupService() {
        this.userRepository = new AuthRepository();
    }

    public SignupResult registerUser(String fullName, String email, String username, String password,
                                     String role, Map<String, String> roleSpecificData) {
        try {
            // Validate inputs
            ValidationResult validation = validateInputs(fullName, email, username, password, role, roleSpecificData);
            if (!validation.isValid()) {
                return new SignupResult(false, validation.getErrorMessage());
            }

            // Check if username or email already exists
            if (userRepository.isUsernameExists(username)) {
                return new SignupResult(false, "Username already exists.");
            }

            if (userRepository.isEmailExists(email)) {
                return new SignupResult(false, "Email already exists.");
            }

            // Generate user ID
            String userID = userRepository.getNextUserID(role);

            // Start transaction (simulated with multiple repository calls)
            boolean userSaved = userRepository.saveUser(userID, username, email, fullName, password, role, "A00001", false);

            if (userSaved) {
                boolean roleDataSaved = saveRoleSpecificData(userID, role, roleSpecificData);

                if (roleDataSaved) {
                    return new SignupResult(true, "Account request submitted successfully! Your account will be activated after administrator approval.", userID);
                } else {
                    // In a real application, you'd rollback the user creation here
                    return new SignupResult(false, "Failed to save role-specific data.");
                }
            } else {
                return new SignupResult(false, "Failed to create user account.");
            }

        } catch (SQLException e) {
            return new SignupResult(false, "Database error: " + e.getMessage());
        }
    }

    private ValidationResult validateInputs(String fullName, String email, String username,
                                            String password, String role, Map<String, String> roleSpecificData) {
        if (fullName == null || fullName.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty() ||
                role == null) {
            return new ValidationResult(false, "Please fill in all required fields.");
        }

        if (!isValidEmail(email)) {
            return new ValidationResult(false, "Please enter a valid email address.");
        }

        if (password.length() < 6) {
            return new ValidationResult(false, "Password must be at least 6 characters long.");
        }

        // Validate role-specific required fields
        String roleValidationError = validateRoleSpecificFields(role, roleSpecificData);
        if (roleValidationError != null) {
            return new ValidationResult(false, roleValidationError);
        }

        return new ValidationResult(true, null);
    }

    private String validateRoleSpecificFields(String role, Map<String, String> roleSpecificData) {
        switch (role) {
            case "Police Officer":
                if (roleSpecificData.get("department") == null || roleSpecificData.get("department").trim().isEmpty()) {
                    return "Please enter your department.";
                }
                if (roleSpecificData.get("rank") == null || roleSpecificData.get("rank").trim().isEmpty()) {
                    return "Please enter your rank.";
                }
                break;
            case "Court Official":
                if (roleSpecificData.get("courtName") == null || roleSpecificData.get("courtName").trim().isEmpty()) {
                    return "Please enter court name.";
                }
                if (roleSpecificData.get("designation") == null || roleSpecificData.get("designation").trim().isEmpty()) {
                    return "Please enter designation.";
                }
                break;
            case "Forensic Expert":
                if (roleSpecificData.get("labName") == null || roleSpecificData.get("labName").trim().isEmpty()) {
                    return "Please enter lab name.";
                }
                break;
        }
        return null;
    }

    private boolean saveRoleSpecificData(String userID, String role, Map<String, String> roleSpecificData) throws SQLException {
        switch (role) {
            case "Police Officer":
                return userRepository.savePoliceOfficer(userID,
                        roleSpecificData.get("rank"),
                        roleSpecificData.get("department"));
            case "Forensic Expert":
                return userRepository.saveForensicExpert(userID,
                        roleSpecificData.get("labName"));
            case "Court Official":
                return userRepository.saveCourtOfficial(userID,
                        roleSpecificData.get("courtName"),
                        roleSpecificData.get("designation"));
            default:
                return false;
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    // Result classes
    public static class SignupResult {
        private final boolean success;
        private final String message;
        private final String userID;

        public SignupResult(boolean success, String message) {
            this(success, message, null);
        }

        public SignupResult(boolean success, String message, String userID) {
            this.success = success;
            this.message = message;
            this.userID = userID;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUserID() { return userID; }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }
}