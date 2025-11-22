package com.fcms.services;

import com.fcms.repositories.AuthRepository;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class PasswordService {
    private final AuthRepository userRepository;

    public PasswordService() {
        this.userRepository = new AuthRepository();
    }

    public PasswordResetResult initiateReset(String userID) {
        try {
            // Validate input
            if (userID == null || userID.trim().isEmpty()) {
                return new PasswordResetResult(false, "Please enter your User ID");
            }

            // Check if user exists
            if (!userRepository.userExists(userID)) {
                return new PasswordResetResult(false, "User ID not found");
            }

            // In a real application, you would:
            // 1. Generate a reset token
            // 2. Send email with reset link
            // 3. Store token in database with expiration

            // For now, we'll just return success
            String email = userRepository.getUserEmail(userID);
            String maskedEmail = maskEmail(email);

            return new PasswordResetResult(true,
                    "Password reset instructions have been sent to " + maskedEmail, userID);

        } catch (SQLException e) {
            return new PasswordResetResult(false, "Database error: " + e.getMessage());
        }
    }

    public PasswordResetResult resetPassword(String userID, String newPassword, String confirmPassword) {
        try {
            // Validate inputs
            ValidationResult validation = validatePasswordReset(userID, newPassword, confirmPassword);
            if (!validation.isValid()) {
                return new PasswordResetResult(false, validation.getErrorMessage());
            }

            // Update password in database
            boolean success = userRepository.updatePassword(userID, newPassword);

            if (success) {
                return new PasswordResetResult(true, "Password has been reset successfully", userID);
            } else {
                return new PasswordResetResult(false, "Failed to reset password");
            }

        } catch (SQLException e) {
            return new PasswordResetResult(false, "Database error: " + e.getMessage());
        }
    }

    private ValidationResult validatePasswordReset(String userID, String newPassword, String confirmPassword) {
        if (userID == null || userID.trim().isEmpty()) {
            return new ValidationResult(false, "User ID is required");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            return new ValidationResult(false, "Please enter a new password");
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return new ValidationResult(false, "Please confirm your new password");
        }

        if (!newPassword.equals(confirmPassword)) {
            return new ValidationResult(false, "Passwords do not match");
        }

        if (newPassword.length() < 6) {
            return new ValidationResult(false, "Password must be at least 6 characters long");
        }

        // Optional: Add more password strength requirements
        if (!isPasswordStrong(newPassword)) {
            return new ValidationResult(false,
                    "Password should include uppercase, lowercase, numbers, and special characters");
        }

        return new ValidationResult(true, null);
    }

    private boolean isPasswordStrong(String password) {
        // At least one uppercase, one lowercase, one digit, one special character
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }

    private String maskEmail(String email) {
        if (email == null) return "your registered email";

        int atIndex = email.indexOf('@');
        if (atIndex <= 2) return "your registered email";

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        String maskedUsername = username.charAt(0) + "***" + username.charAt(username.length() - 1);
        return maskedUsername + domain;
    }

    // Result classes
    public static class PasswordResetResult {
        private final boolean success;
        private final String message;
        private final String userID;

        public PasswordResetResult(boolean success, String message) {
            this(success, message, null);
        }

        public PasswordResetResult(boolean success, String message, String userID) {
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