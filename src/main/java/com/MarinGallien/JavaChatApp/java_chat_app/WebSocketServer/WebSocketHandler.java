package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.OnlineStatusMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.WebSocketDbService;

import com.MarinGallien.JavaChatApp.java_chat_app.Enums.OnlineStatus;
import com.MarinGallien.JavaChatApp.java_chat_app.OfflineMessageService.OfflineMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Set;

@Controller
public class WebSocketHandler {
    // Parameters
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private final WebSocketDbService databaseService;
    private final StatusManager statusManager;
    private final ChatManager chatManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final OfflineMessageService offlineMessageService;

    // Constructor
    public WebSocketHandler(WebSocketDbService databaseService, StatusManager statusManager, ChatManager chatManager,
                            SimpMessagingTemplate messagingTemplate, OfflineMessageService offlineMessageService) {
        this.databaseService = databaseService;
        this.statusManager = statusManager;
        this.chatManager = chatManager;
        this.messagingTemplate = messagingTemplate;
        this.offlineMessageService = offlineMessageService;
    }


    // THIS METHOD needs to be  updated because it does not handle offline users
    // Handles text messages sent to specific chat chats
    @MessageMapping("/chat/{chatId}")
    public void handleTextMessage(@DestinationVariable String chatId, @Payload WebSocketMessage message) {
        try {
            String senderId = message.getSenderID();
            logger.info("Processing text message from {} to chat {}", senderId, chatId);

            // Save message to database and return
            databaseService.saveMessage(message);

            String messageChatId = message.getChatID();
            Set<String> chatParticipants = chatManager.getChatParticipants(messageChatId);

            for (String userId : chatParticipants) {
                if (statusManager.isOnline(userId)) {
                    messagingTemplate.convertAndSendToUser(userId, "/queue/messages", message);
                } else {
                    // Store message in Redis queue for offline user
                    boolean stored = offlineMessageService.storeOfflineMessage(userId, message);
                    if (!stored) {
                        logger.warn("Failed to queue message for user {}", userId);
                    }
                    logger.info("Queued message for offline user {}", userId);
                }
            }

        } catch (Exception e) {
            logger.error("Error processing text message", e);
        }
    }



    // Method notifies a user's contacts of a status change
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
            logger.error("Error notifying contacts of status change for user: {}", userId);
        }
    }

    private void deliverPendingMessages(String userId) {
        try {
            // Check if the user has any pending messages
            if (!offlineMessageService.hasPendingMessages(userId)) {
                logger.info("No pending messages for user {}", userId);
                return;
            }

            // Retrieve all pending messages
            List<WebSocketMessage> pendingMessages = offlineMessageService.retrievePendingMessages(userId);

            // Send each message to the user
            for (WebSocketMessage message : pendingMessages) {
                messagingTemplate.convertAndSendToUser(userId, "/queue/messages", message);
            }

            logger.info("Successfully delivered pending messages to user {}", userId);

        } catch (Exception e) {
            logger.error("Failed to deliver pending messages to user {}", userId);
        }
    }

    // Handle connection - Save user ID in session attributes and update online status in database
    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectEvent event) {
        try {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
            String userId = getUserIdFromSession(headerAccessor);

            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Could not updated online status: user ID is null");
                return;
            }

            // NEED TO CHECK USER ID EXISTS

            // Update user status in database
            boolean statusUpdated = databaseService.saveStatus(userId, OnlineStatus.ONLINE);

            // Update status in status manager
            statusManager.setUserOnline(userId);

            if (!statusUpdated) {
                logger.warn("Failed to update status to ONLINE for user {}", userId);
            }

            // Notify contacts of status change
            notifyContactsOfStatusChange(userId, OnlineStatus.ONLINE);

            // Send any pending messages
            deliverPendingMessages(userId);

        } catch (Exception e) {
            logger.error("Error handling websocket connection", e);
        }
    }

    // Handle disconnection - retrieve user ID from session attributes and update online status in database
    @EventListener
    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent event) {
        try {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
            String userId = getUserIdFromSession(headerAccessor);

            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Could not update online status: user ID is null or empty");
                return;
            }

            // NEED TO CHECK USER ID EXISTS

            // Update user status in database
            boolean statusUpdated = databaseService.saveStatus(userId, OnlineStatus.OFFLINE);

            // Update status in status manager
            statusManager.setUserOffline(userId);

            if (!statusUpdated) {
                logger.warn("Failed to update user status to OFFLINE for user {}", userId);
                return;
            }

            // Notify contacts of status change
            notifyContactsOfStatusChange(userId, OnlineStatus.OFFLINE);

        } catch (Exception e) {
            logger.warn("Error handling websocket disconnection", e);
        }
    }

    // Retrieve user ID from session attributes
    private String getUserIdFromSession(SimpMessageHeaderAccessor headerAccessor) {
        // Retrieve user ID from session attributes
        Object userId = headerAccessor.getSessionAttributes().get("userId");
        return userId != null ? userId.toString() : null;
    }

}
