package org.example.Util;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AuthValidator {

    // Email validation via Apache Commons Validator
    public static boolean isValidEmail(String email) {
        return email != null && EmailValidator.getInstance().isValid(email);
    }

    // Password regex:
    // - At least one uppercase letter
    // - At least one lowercase letter
    // - At least one digit
    // - At least one special character
    // - Minimum 8 characters
    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";

    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_REGEX);

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    /**
     * Returns a specific error message for password validation.
     */
    public static String getPasswordValidationError(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password is required.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!containsUpperCase(password)) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!containsLowerCase(password)) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!containsDigit(password)) {
            return "Password must contain at least one number.";
        }
        if (!containsSpecialChar(password)) {
            return "Password must contain at least one special character.";
        }
        return null; // no error
    }

    // Helper methods for individual checks (more granular than full regex)

    private static boolean containsUpperCase(String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }

    private static boolean containsLowerCase(String password) {
        return password.chars().anyMatch(Character::isLowerCase);
    }

    private static boolean containsDigit(String password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    private static boolean containsSpecialChar(String password) {
        return password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
    }
}