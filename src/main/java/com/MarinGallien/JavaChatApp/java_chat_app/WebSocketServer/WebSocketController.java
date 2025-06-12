package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.OnlineStatusMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.WebSocketDatabaseService;

import com.MarinGallien.JavaChatApp.java_chat_app.Enums.OnlineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;

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
    public Message handleTextMessage(@DestinationVariable String roomId, @Payload WebSocketMessage message,
                                              SimpMessageHeaderAccessor headerAccessor) {
        try {
            String senderId = message.getSenderID();
            logger.info("Processing text message from {} to room {}", senderId, roomId);

            // Save message to database and return
            return databaseService.saveMessage(message);
        } catch (Exception e) {
            logger.error("Error processing text message", e);
            return null;
        }
    }

    // Handles online presence updates
    @MessageMapping("/presence")
    public void handleContactPresenceUpdate(@Payload OnlineStatusMessage presenceMessage) {
        try {
            // Extract fields
            String userId = presenceMessage.getSenderID();
            OnlineStatus status = presenceMessage.getStatus();

            // Update status in database
            boolean statusUpdated = databaseService.saveStatus(userId, status);
            if (!statusUpdated) {
                logger.warn("Failed to update status for user {}", userId);
                return;
            }
        } catch (Exception e) {
            logger.error("Error processing status update", e);
        }
    }

    // We need a method handleSelfPresenceUpdate to change our own status in the database when websocket connection is closedwds

    private void notifyContactsOfStatusChange(String userId, OnlineStatus status) {
        try {
            // Retrieve list of contacts from the database
            List<String> contactIds = databaseService.getContacts(userId);

            // Create a status message
            OnlineStatusMessage statusMessage = new OnlineStatusMessage(status, userId);

            // Broadcast status message to each contact
            for (String contactId : contactIds) {
                messagingTemplate.convertAndSendToUser(
                        contactId,
                        "/queue/presence",
                        statusMessage
                );
            }
        } catch (Exception e) {
            logger.error("Error noptifying contacts of status change for user: {}", userId);
        }
    }

}