package com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.UserRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserDbService {

    private static final Logger logger = LoggerFactory.getLogger(UserDbService.class);

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

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
}
