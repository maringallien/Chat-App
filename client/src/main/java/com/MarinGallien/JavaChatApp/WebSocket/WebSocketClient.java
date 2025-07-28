package com.MarinGallien.JavaChatApp.WebSocket;
import com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages.OnlineStatusMessage;
import com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.Database.Message;
import com.MarinGallien.JavaChatApp.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Component
public class WebSocketClient {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private final WebSocketStompClient stompClient;
    private StompSession session;
    private MessageHandler messageHandler;

    // Simple callback interface for receiving messages and connection events
    public interface MessageHandler {
        Message onMessage(WebSocketMessage message);        // Handle incoming chat messages
        void onStatusUpdate(OnlineStatusMessage status);    // Handle user status changes
        void onConnected();                                 // Handle successful connection
        void onDisconnected();                              // Handle disconnection
        void onError(String error);                         // Handle errors
    }

    public WebSocketClient() {
        this.stompClient = createStompClient();
    }


    // ========== CONNECTION HANDLING METHODS ==========

    // Create WebSocket STOMP client
    private WebSocketStompClient createStompClient() {
        // Create SockJS client with WebSocket transport
        SockJsClient sockJsClient = new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))
        );

        // Create STOMP client using the SockJs client as transport
        WebSocketStompClient client = new WebSocketStompClient(sockJsClient);

        // Configure JSON message conversion for auto serialization/deserialization
        client.setMessageConverter(new MappingJackson2MessageConverter());

        // Set up task scheduler for handling connection timeouts and heartbeats
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        client.setTaskScheduler(scheduler);

        return client;
    }

    // Connect to the chat server asynchronously using JWT authentication
    public CompletableFuture<Void> connect (MessageHandler handler) {
        // Load session parameters
        String userId = UserSession.getUserId();
        String jwtToken = UserSession.getJwtToken();
        String url = UserSession.getWsBaseUrl();

        // Initialize handler
        this.messageHandler = handler;

        // Create WebSocket headers for HTTP-level authentication
        WebSocketHttpHeaders webSocketHeaders = new WebSocketHttpHeaders();
        webSocketHeaders.add("Authorization", "Bearer " + jwtToken);

        // Create STOMP headers with user identification and JWT authentication
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("userId", userId);

        // Attempt async connection and set up subscriptions on success
        return stompClient.connectAsync (url, webSocketHeaders, stompHeaders, new SessionHandler())
                .thenAccept(session -> {
                    // Store the active session
                    this.session = session;

                    // Auto-subscribe to personal message queue (server sends messages here)
                    session.subscribe("/queue/messages", new MessageFrameHandler());

                    // Auto-subscribe to presence updates (online/offline status changes)
                    session.subscribe("/queue/presence", new PresenceFrameHandler());

                    // Notify handler that connection is ready
                    if (handler != null) handler.onConnected();
                });
    }

    // Disconnect from the server
    public void disconnect() {
        if (session != null && session.isConnected()) {session.disconnect();}
    }

    // Check if session is open
    public boolean isConnected() {
        return session != null && session.isConnected();
    }


    // ========== COMMUNICATION METHODS ==========

    // Send chat message to a specific person
    public void sendMessage(WebSocketMessage message) {
        if (session != null && session.isConnected()) {

            // Send to server's chat endpoint
            session.send("/app/chat/" + message.getChatID(), message);
        }
    }

    // Send a status update to the server
    public void sendStatus(OnlineStatusMessage status) {
        if (session != null && session.isConnected()) {

            // Send to server's status endpoint
            session.send("/app/status", status);
        }
    }


    // ========== INTERNAL SESSION HANDLER CLASS ==========

    // This class essentially performs event handling
    private class SessionHandler extends StompSessionHandlerAdapter {

        // Called when connection is successfully established
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            logger.info("Connected to chat server");
        }

        // Called when STOMP protocol errors occur
        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                    byte[] payload, Throwable exception) {
            logger.error("STOMP error", exception);

            // Forward error to message handler
            if (messageHandler != null) {messageHandler.onError(exception.getMessage());}
        }

        // Called when websocket transport fails
        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            logger.error("Transport error", exception);

            // Notify handler of disconnection
            if (messageHandler != null) {messageHandler.onDisconnected();}
        }
    }


    // ========== INTERNAL CHAT FRAME HANDLER CLASS ==========

    // This class handles incoming WebSocketMessage objects
    private class MessageFrameHandler implements StompFrameHandler {

        // Tell STOMP what type of object to deserialize incoming messages into
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return WebSocketMessage.class;
        }

        // Handle incoming message frames
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            // Verify payload type and forward to message handler
            if (payload instanceof WebSocketMessage message && messageHandler != null) {
                messageHandler.onMessage(message);
            }
        }
    }


    // ========== INTERNAL STATUS FRAME HANDLER CLASS ==========

    // Handles incoming OnlineStatusMessage objects
    private class PresenceFrameHandler implements StompFrameHandler {

        // Tell STOMP what type of object to deserialize incoming messages into
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return OnlineStatusMessage.class;
        }

        // Handle incoming status update frames
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            // Verify payload type and forward to message handler
            if (payload instanceof OnlineStatusMessage status && messageHandler != null) {
                messageHandler.onStatusUpdate(status);
            }
        }
    }

}
