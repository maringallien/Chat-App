package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.SessionDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.OnlineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static Logger logger = LoggerFactory.getLogger(SessionService.class);

    @Autowired
    SessionDbService sessionDbService;

    public boolean updateUserStatus(String userId, OnlineStatus status) {
        try {
            // Validate inputs
            if (!validateId(userId)) {
                logger.warn("Failed to update user status: user ID is null or empty");
                return false;
            }

            if (status == null) {
                logger.warn("Failed to update user status: provided status is null");
                return false;
            }

            // Update database
            OnlineStatus newStatus = sessionDbService.updateStatus(userId, status);

            // Check that update was successful
            if (newStatus == null || newStatus != status) {
                logger.info("Failed to update user's status");
                return false;
            }

            logger.info("Successfully updated user status to {}", newStatus);
            return true;

        } catch (Exception e) {
            logger.error("Error occurred when updating user status: {}", e.getMessage());
            return false;
        }
    }

    private boolean validateId(String Id) {
        return Id != null && !Id.trim().isEmpty();
    }
}
