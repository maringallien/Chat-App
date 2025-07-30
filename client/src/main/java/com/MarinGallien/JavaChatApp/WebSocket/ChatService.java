package com.MarinGallien.JavaChatApp.WebSocket;

import com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages.OnlineStatusMessage;
import com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.ContactDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Contact;
import com.MarinGallien.JavaChatApp.Database.JPARepos.ContactRepo;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Message;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import com.MarinGallien.JavaChatApp.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatService implements WebSocketClient.MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final WebSocketClient webSocketClient;
    private MessageListener messageListener;
    private boolean connected = false;
    private String userId;
    private final ContactDbService contactDbService;

    // Interface for components that want to receive chat messages
    public interface MessageListener {
        void onMessageReceived(String senderId, String message);
        void onStatusChanged(String userId, OnlineStatus status);
        void onConnectionChanged(boolean connected);
        void onError(String error);
    }

    public ChatService(WebSocketClient webSocketClient, ContactDbService contactDbService) {
        this.webSocketClient = webSocketClient;
        userId = UserSession.getInstance().getUserId();
        this.contactDbService = contactDbService;
    }


    // ========== LISTENER MANAGEMENT ==========

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }


    // ========== CONNECTION MANAGEMENT METHODS ==========

    // Start chat session
    public void startChat() {
        // Attempt to connect to the WebSocket server with JWT authentication
        webSocketClient.connect(this)
            .thenRun(() -> {
                connected = true;
                logger.info("Chat started");

                // Broadcast online status to contacts
                sendOnlineStatus(OnlineStatus.ONLINE);

                // Notify listener
                if (messageListener != null) {
                    messageListener.onConnectionChanged(true);
                }
            })
            .exceptionally(error -> {
                connected = false;
                logger.error("Failed to connect: {}", error.getMessage());

                // Notify message listener
                if (messageListener != null) {
                    messageListener.onError("Failed to connect: " + error.getMessage());
                }
                return null;
            });
    }

    public void stopChat() {
        sendOnlineStatus(OnlineStatus.OFFLINE);
        webSocketClient.disconnect();
        connected = false;

        if (messageListener != null) {
            messageListener.onConnectionChanged(false);
        }
    }

    public boolean isConnected() {
        return connected;
    }


    // ========== COMMUNICATION METHODS =========

    public void sendMessage(String chatId, String content) {
        if (!connected) {
            logger.warn("Cannot send message: not connected");
            return;
        }

        WebSocketMessage message = new WebSocketMessage(userId, chatId, content);
        webSocketClient.sendMessage(message);
    }

    private void sendOnlineStatus(OnlineStatus status) {
        if (!connected) {
            logger.warn("Cannot send message: not connected");
            return;
        }

        OnlineStatusMessage statusMessage = new OnlineStatusMessage(status, userId);
        webSocketClient.sendStatus(statusMessage);
    }


    // ========== WebsocketClient.MessageHandler INTERFACE IMPLEMENTATION ==========

    @Override
    public Message onMessage(WebSocketMessage message) {
        logger.debug("Received message from {}: {}", message.getSenderID(), message.getContent());

        if (messageListener != null) {
            messageListener.onMessageReceived(message.getSenderID(), message.getContent());
        }

        // Create Message object for potential database storage
        Message dbMessage = new Message();
        dbMessage.setMessageId(message.getMessageID());
        dbMessage.setSenderId(message.getSenderID());
        dbMessage.setChatId(message.getChatID());
        dbMessage.setContent(message.getContent());

        return dbMessage;
    }

    @Override
    public void onStatusUpdate(OnlineStatusMessage status) {
        logger.debug("Received status update from {}: {}", status.getSenderID(), status.getStatus());

        // Update contact's status in database
        Contact contact = contactDbService.findContactById(status.getSenderID());
        if (contact != null) {
            contact.setStatus(status.getStatus());
            contactDbService.save(contact);
        }

        if (messageListener != null) {
            messageListener.onStatusChanged(status.getSenderID(), status.getStatus());
        }
    }

    @Override
    public void onConnected() {
        logger.info("WebSocket connection established");
        connected = true;
        if (messageListener != null) {
            messageListener.onConnectionChanged(true);
        }
    }

    @Override
    public void onDisconnected() {
        logger.info("WebSocket connection lost");
        connected = false;
        if (messageListener != null) {
            messageListener.onConnectionChanged(false);
        }
    }

    @Override
    public void onError(String error) {
        logger.error("WebSocket error: {}", error);
        if (messageListener != null) {
            messageListener.onError("Connection error: " + error);
        }
    }
}


