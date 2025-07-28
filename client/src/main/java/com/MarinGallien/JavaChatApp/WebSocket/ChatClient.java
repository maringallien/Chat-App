package com.MarinGallien.JavaChatApp.WebSocket;

import com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages.OnlineStatusMessage;
import com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.Database.Message;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import com.MarinGallien.JavaChatApp.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatClient implements WebSocketClient.MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);
    private static String userId;
    private final WebSocketClient webSocketClient;

    public ChatClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
        userId = UserSession.getUserId();
    }


    // ========== CONNECTION MANAGEMENT METHODS ==========

    // Start chat session
    public void startChat() {

        // Attempt to connect to the WebSocket server with JWT authentication
        webSocketClient.connect(this)
            .thenRun(() -> {
                System.out.println("Chat started");

                // Broadcast online status to contacts
                webSocketClient.sendStatus(new OnlineStatusMessage(OnlineStatus.ONLINE, userId));
            })
            .exceptionally(error -> {
                // Connection failed - display error message
                System.err.println("Failed to connect: " + error.getMessage());
                return null; // Required for CompletableFuture.exceptionally()
            });
    }

    public void stopChat() {
        webSocketClient.disconnect();
    }


    // ========== COMMUNICATION METHODS =========

    public void sendMessage(String chatId, String content, String recipientId) {
        WebSocketMessage message = new WebSocketMessage(userId, chatId, content, recipientId);
        webSocketClient.sendMessage(message);
    }

    private void sendOnlineStatus() {
        OnlineStatusMessage status = new OnlineStatusMessage(OnlineStatus.ONLINE, userId);
        webSocketClient.sendStatus(status);
    }


    // ========== INTERFACE METHODS ==========

    @Override
    public Message onMessage(WebSocketMessage message) {
        // Create Message object
        // Persist in database
        // Return Message object for display
    }

    @Override
    public void onStatusUpdate(OnlineStatusMessage status) {
        // Update contact status in database
    }

    @Override
    public void onConnected() {
        // Define here what to do when connection established
    }

    @Override
    public void onDisconnected() {
        // Define here what to do when connection terminated
    }

    @Override
    public void onError(String error) {
        // Define here what to do when error is encountered
    }
}


