package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.UserDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.OnlineStatus;
import com.MarinGallien.JavaChatApp.java_chat_app.Services.AuthService.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service needs methods to handle:
 * crating account
 * closing account
 *
 * updating username
 * updating email
 * updating password
 * updating status
 *
 */


@Service
public class UserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserDbService userDbService;

    @Autowired
    private JWTService jwtService;

    public User createUser(String username, String email, String password) {
        try {
            // Validate input
            if (!validateString(username) || !validateEmail(email) || !validateString(password)) {
                logger.warn("Registration failed: invalid registration input");
                return null;
            }

            if (username.length() <= 3 || username.length() >= 50 || password.length() <= 6) {
                logger.warn("Registration failed: invalid registration input");
                return null;
            }

            // Create user
            User user = userDbService.createUser(username, email, password);

            // Make sure user was created
            if (user == null) {
                logger.warn("Failed to create user: Email or username already exists");
                return null;
            }

            // Return new user
            logger.info("Successfully registered user: {} with email {}", user.getUserId(), user.getEmail());
            return user;

        } catch (Exception e) {
            logger.error("Error encountered when registering user: {}", e.getMessage());
            return null;
        }
    }

    public Boolean login(String email, String password) {
        try {
            // Validate input parameters
            if (!validateEmail(email) || !validateString(password)) {
                logger.warn("Login failed: invalid username or password input");
                return null;
            }

            Boolean loggedIn = userDbService.login(email, password);

            if (!loggedIn) {
                logger.error("User found during validation but not found during retrieval");
                return null;
            }

            logger.info("Successful login for user with email {}", email);
            return loggedIn;

        } catch (Exception e) {
            logger.error("Error occurred when logging in: {}", e.getMessage());
            return null;
        }
    }

    public boolean deleteUser(String userId, String password) {
        try {
            // Validate input
            if (!validateId(userId) || !validateString(password)) {
                logger.warn("Failed to delete user: invalid input parameters");
                return false;
            }

            // Call database service
            boolean deleted = userDbService.deleteUser(userId, password);

            if (!deleted) {
                logger.warn("Failed to delete user {}", userId);
                return false;
            }

            logger.info("Successfully deleted user {}", userId);
            return true;

        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            return false;
        }
    }

    public String updateUsername(String userId, String username) {
        try {
            // Validate input
            if (!validateId(userId) || !validateString(username)) {
                logger.warn("Failed to update username: invalid input parameters");
                return null;
            }

            if (username.length() <= 3 || username.length() >= 50) {
                logger.warn("Failed to update username: username must be between 3 and 50 characters");
                return null;
            }

            // Call database service
            String updatedUsername = userDbService.updateUsername(userId, username);

            if (updatedUsername == null) {
                logger.warn("Failed to update username for user {}", userId);
                return null;
            }

            logger.info("Successfully updated username for user {}", userId);
            return updatedUsername;

        } catch (Exception e) {
            logger.error("Error updating username: {}", e.getMessage());
            return null;
        }
    }

    public String updateEmail(String userId, String email) {
        try {
            // Validate input
            if (!validateId(userId) || !validateEmail(email)) {
                logger.warn("Failed to update email: invalid input parameters");
                return null;
            }

            // Call database service
            String updatedEmail = userDbService.updateEmail(userId, email);

            if (updatedEmail == null) {
                logger.warn("Failed to update email for user {}", userId);
                return null;
            }

            logger.info("Successfully updated email for user {}", userId);
            return updatedEmail;

        } catch (Exception e) {
            logger.error("Error updating email: {}", e.getMessage());
            return null;
        }
    }

    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        try {
            // Validate input
            if (!validateId(userId) || !validateString(oldPassword) || !validateString(newPassword)) {
                logger.warn("Failed to update password: invalid input parameters");
                return false;
            }

            if (newPassword.length() <= 6) {
                logger.warn("Failed to update password: password must be at least 6 characters");
                return false;
            }

            // Call database service with new password
            boolean updated = userDbService.updatePassword(userId, oldPassword, newPassword);

            if (!updated) {
                logger.warn("Failed to update password for user {}", userId);
                return false;
            }

            logger.info("Successfully updated password for user {}", userId);
            return true;

        } catch (Exception e) {
            logger.error("Error updating password: {}", e.getMessage());
            return false;
        }
    }

    public OnlineStatus updateStatus(String userId, OnlineStatus status) {
        try {
            // Validate input
            if (!validateId(userId) || status == null) {
                logger.warn("Failed to update status: invalid input parameters");
                return null;
            }

            // Call database service
            OnlineStatus updatedStatus = userDbService.updateStatus(userId, status);

            if (updatedStatus == null) {
                logger.warn("Failed to update status for user {}", userId);
                return null;
            }

            logger.info("Successfully updated status for user {} to {}", userId, updatedStatus);
            return updatedStatus;

        } catch (Exception e) {
            logger.error("Error updating status: {}", e.getMessage());
            return null;
        }
    }

    private boolean validateEmail(String email) {
        return email != null &&
                !email.trim().isEmpty() &&
                email.contains("@") &&
                email.contains(".");
    }

    private boolean validateId(String id) {
        return id != null && !id.trim().isEmpty();
    }

    private boolean validateString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
