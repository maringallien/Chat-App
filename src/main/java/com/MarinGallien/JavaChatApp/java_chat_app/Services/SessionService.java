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

    private final SessionDbService sessionDbService;

    public SessionService(SessionDbService sessionDbService) {
        this.sessionDbService = sessionDbService;
    }

    public OnlineStatus updateUserStatus(String userId, OnlineStatus status) {
        try {
            // Validate inputs
            if (!validateId(userId)) {
                logger.warn("Failed to update user status: user ID is null or empty");
                return null;
            }

            if (status == null) {
                logger.warn("Failed to update user status: provided status is null");
                return null;
            }

            // Update database
            OnlineStatus newStatus = sessionDbService.updateStatus(userId, status);

            // Check that update was successful
            if (newStatus == null || newStatus != status) {
                logger.info("Failed to update user's status");
                return null;
            }

            logger.info("Successfully updated user status to {}", newStatus);
            return newStatus;

        } catch (Exception e) {
            logger.error("Error occurred when updating user status: {}", e.getMessage());
            return null;
        }
    }

    private boolean validateId(String Id) {
        return Id != null && !Id.trim().isEmpty();
    }
}
