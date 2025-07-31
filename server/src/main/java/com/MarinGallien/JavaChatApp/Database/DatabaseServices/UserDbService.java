package com.MarinGallien.JavaChatApp.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.JPAEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.UserRepo;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserDbService {

    private static final Logger logger = LoggerFactory.getLogger(UserDbService.class);

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserDbService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // Creates a new user with encrypted password
    public User createUser(String username, String email, String password) {
        try {
            // Check if user already exists
            if (userRepo.existsByEmail(email)) {
                logger.warn("Cannot create user: email {} already exists", email);
                return null;
            }

            if (userRepo.existsByUsername(username)) {
                logger.warn("Cannot create user: username {} already exists", username);
                return null;
            }

            // Hash the password
            String hashedPassword = passwordEncoder.encode(password);

            // Create the user
            User user = new User(username, email, hashedPassword);
            User savedUser = userRepo.save(user);

            logger.info("Successfully created and saved user: {} with email {}", username, email);
            return savedUser;

        } catch (Exception e) {
            logger.error("Failed to create user: {}", e.getMessage());
            return null;
        }
    }

    public boolean login(String email, String password) {
        try {
            if (!validateCredentials(email, password)) {
                logger.info("Failed login attempt: invalid credentials");
                return false;
            }

            logger.info("Successfully logged-in user");
            return true;

        } catch (Exception e) {
            logger.error("Failed login attempt");
            return false;
        }
    }

    public boolean deleteUser(String userId, String password) {
        try {
            // Validate input
            if (!userRepo.existsById(userId)) {
                logger.warn("Failed to delete user account: user {} does not exist", userId);
                return false;
            }

            // Make sure password is correct
            User user = userRepo.findUserById(userId);
            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                logger.warn("Failed to delete user:provided password is wrong");
                return false;
            }

            // Delete user
            userRepo.delete(user);

            logger.info("Successfully deleted user {} from database", userId);
            return true;

        } catch (Exception e) {
            logger.error("Error deleting user");
            return false;
        }
    }

    public String updateUsername(String userId, String username) {
        try {
            // Validate input
            if (!userRepo.existsById(userId)) {
                logger.warn("Failed to update username: user {} does not exist", userId);
                return null;
            }

            // Make sure new username is different
            if (userRepo.findUserById(userId).getUsername().equals(username.trim())) {
                logger.warn("Failed to update username: current and new usernames are identical");
                return null;
            }

            User user = userRepo.findUserById(userId);

            // Update username
            user.setUsername(username);
            userRepo.save(user);

            return user.getUsername();

        } catch (Exception e) {
            logger.error("Error updating username");
            return null;
        }
    }

    public String updateEmail(String userId, String email) {
        try {
            // Validate input
            if (!userRepo.existsById(userId)) {
                logger.warn("Failed to update email: user {} does not exist", userId);
                return null;
            }

            // Make sure new email is different
            if (userRepo.findUserById(userId).getEmail().equals(email.trim())) {
                logger.warn("Failed to update email: current and new emails are identical");
                return null;
            }

            User user = userRepo.findUserById(userId);

            // update email
            user.setEmail(email);
            userRepo.save(user);

            logger.info("Successfully updated user's email to {}", email);

            return user.getEmail();

        } catch (Exception e) {
            logger.error("Error updating email");
            return null;
        }
    }

    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        try {
            // Validate input
            if (!userRepo.existsById(userId)) {
                logger.warn("Failed to update password: user {} does not exist", userId);
                return false;
            }

            // First verify old password is correct
            User user = userRepo.findUserById(userId);
            if (!validateCredentials(user.getEmail(), oldPassword)) {
                logger.warn("Failed to update password: old password is incorrect");
                return false;
            }

            // Make sure password is different
            if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
                logger.warn("Failed to update password: current and new passwords are identical");
                return false;
            }

            // Hash new password
            String hashedNewPassword = passwordEncoder.encode(newPassword);

            // Store new password
            user.setPasswordHash(hashedNewPassword);

            logger.info("Successfully updated password for user {}", userId);
            return true;

        } catch (Exception e) {
            logger.error("Error updating password");
            return false;
        }
    }

    public OnlineStatus updateStatus(String userId, OnlineStatus status) {
        try {
            // Validate input
            if (!userRepo.existsById(userId)) {
                logger.warn("Failed to update status: user {} does not exist", userId);
                return null;
            }

            if (status == null) {
                logger.warn("Failed to update status: status is null");
            }

            User user = userRepo.findUserById(userId);

            // Update the status
            user.setStatus(status);
            userRepo.save(user);

            logger.info("Successfully updated user status to {}", status);
            return user.getStatus();

        } catch (Exception e) {
            logger.error("Error updating status");
            return null;
        }
    }

    public boolean validateCredentials(String email, String password) {
        try {
            User user = userRepo.findUserByEmail(email);
            if (user == null) {
                logger.warn("Failed to verify user credentials: user not found for email {}", email);
                return false;
            }

            boolean matches = passwordEncoder.matches(password, user.getPasswordHash());

            if (!matches) {
                logger.warn("Failed to verify user credentials: password does not match anything");
                return false;
            }

            logger.info("Successfully verified user credentials");
            return matches;

        } catch (Exception e) {
            logger.error("Error validating user credentials: {}", e.getMessage());
            return false;
        }
    }

    public User getUserByEmail(String email) {
        try {
            User user = userRepo.findUserByEmail(email);

            if (user == null) {
                logger.warn("No user found with email: {}", email);
                return null;
            }

            logger.info("Successfully retrieve user with email {}", email);
            return user;

        } catch (Exception e) {
            logger.error("Failed to retrieve user by email: {}", e.getMessage());
            return null;
        }
    }
}
