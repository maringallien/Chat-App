package com.MarinGallien.JavaChatApp.WebSocketServer;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StatusManager {

    private static final Logger logger = LoggerFactory.getLogger(StatusManager.class);

    // User Online Status map - userID:OnlineStatus
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    // Adds new user to map
    public void setUserOnline(String userId) {
        onlineUsers.add(userId);
        logger.info("User {} was added to online list", userId);
    }

    // Removes user from map
    public void setUserOffline(String userId) {
        onlineUsers.remove(userId);
        logger.info("User {} was removed from online list", userId);
    }

    // Check if user is online
    public boolean isOnline(String userId) {
        return onlineUsers.contains(userId);
    }
}
