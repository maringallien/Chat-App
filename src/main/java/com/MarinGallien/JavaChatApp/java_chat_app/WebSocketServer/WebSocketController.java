package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.OnlineStatusMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.WebSocketDatabaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    // Parameters
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    private final WebSocketDatabaseService databaseService;
    private final ConnManager connManager;
    private final RoomManager roomManager;

    // Constructor
    public WebSocketController(WebSocketDatabaseService databaseService, ConnManager connManager,
                               RoomManager roomManager) {
        this.databaseService = databaseService;
        this.connManager = connManager;
        this.roomManager = roomManager;
    }


    // Handles text messages sent to specific chat rooms
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public WebSocketMessage handleTextMessage(@DestinationVariable String roomId, @Payload WebSocketMessage message,
                                              SimpMessageHeaderAccessor headerAccessor) {
        try {
            String senderId = message.getClientID();
            logger.info("Processing text message from {} to room {}", senderId, roomId);

            // Perform null/empty check on message content
            if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                logger.warn("Empty message content from user {}", senderId);
                return null;
            }

            // Verify user exists
            if (!databaseService.userExists(senderId)) {
                logger.warn("User {} does not exist", senderId);
                return null;
            }

            // Verify room exists
            if (databaseService.roomExists(roomId)) {
                logger.warn("Room {} does not exist", roomId);
                return null;
            }

            // Verify user is in the chatroom
            if (!databaseService.isUserInChat(senderId, roomId)) {
                logger.warn("User {} does not belong to chat {}", senderId, roomId);

            }

            // Save message to database
            Message savedMessage = databaseService.saveMessage(message);

        } catch (Exception e) {
            logger.error("Error processing text message", e);
            return null;
        }
    }

    // Handles online presence updates
    @MessageMapping("/presence")
    public void handlePresenceUpdate(@Payload OnlineStatusMessage presenceMessage) {

    }

    // Helper method to check if user exists and is allowed to chat in the room
    private boolean checkRoomPermissions(String userId, String roomId) {

    }
}