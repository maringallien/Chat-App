package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

public class RoomManager {
    private static final Logger logger = LoggerFactory.getLogger(RoomManager.class);

    // Thread safe map to store roomID:Set<userID>
    private final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();
}
