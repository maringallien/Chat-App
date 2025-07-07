package com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.UserRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.OnlineStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class SessionDbService {

    private static Logger logger = LoggerFactory.getLogger(SessionDbService.class);

    private final UserRepo userRepo;

    public SessionDbService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public OnlineStatus updateStatus(String userId, OnlineStatus status) {
        try {
            // Perform input check
            if (!userRepo.existsById(userId)) {
                logger.warn("Failed to update online status: user {} does not exist", userId);
                return null;
            }

            // Update status
            User user = userRepo.findUserById(userId);
            user.setStatus(status);

            // Return updated status
            return user.getStatus();

        } catch (Exception e) {
            logger.error("Failed to update user's online status: {}", e.getMessage());
            return null;
        }
    }
}
