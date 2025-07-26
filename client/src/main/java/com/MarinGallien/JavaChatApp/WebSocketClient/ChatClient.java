package com.MarinGallien.JavaChatApp.WebSocketClient;

import com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages.OnlineStatusMessage;
import com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatClient implements WebSocketClient.MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);
    private final WebSocketClient webSocketClient;

    public ChatClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }


    // ========== CONNECTION MANAGEMENT METHODS ==========

    // Start chat session
    public void startChat(String userId, String jwtToken) {
        // Validate inputs
        if (userId == null || userId.trim().isEmpty()) {
            logger.error("Cannot start chat: User ID is null or empty");
            return;
        }

        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            logger.error("Cannot start chat: jwt token is null or empty");
            return;
        }

        // Attempt to connect to the WebSocket server with JWT authentication
        webSocketClient.connect("ws://localhost:8080/ws", userId, jwtToken, this)
            .thenRun(() -> {
                System.out.println("Chat started for user: " + userId);

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
    public void onMessage(WebSocketMessage message) {
        // Define here what you should do when message is received
    }

    @Override
    public void onStatusUpdate(OnlineStatusMessage status) {
        // Define here what tyo do when online status up[date is received
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


