package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.UserDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Services.AuthService.AuthService;
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
    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    UserDbService userDbService;

    @Autowired
    private JWTService jwtService;

    public User register(String username, String email, String password) {
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

    public User login(String email, String password) {
        try {
            // Validate input parameters
            if (!validateEmail(email) || !validateString(password)) {
                logger.warn("Login failed: invalid username or password input");
                return null;
            }

            boolean validCredentials = userDbService.validateCredentials(email, password);

            if(!validCredentials) {
                logger.warn("Failed to login: invalid credentials for email {}", email);
                return null;
            }

            User user = userDbService.findUserByEmail(email);

            if (user == null) {
                logger.error("User found during validation but not found during retrieval");
                return null;
            }

            logger.info("Successful login for user {} with email {}", user.getUsername(), user.getEmail());
            return user;

        } catch (Exception e) {
            logger.error("Error occurred when logging in: {}", e.getMessage());
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
