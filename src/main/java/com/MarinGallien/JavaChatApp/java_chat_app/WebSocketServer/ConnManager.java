package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import org.springframework.web.socket.WebSocketSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class ConnManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnManager.class);

    // Connections map - userID:WebSocketSession
    private final Map<String, WebSocketSession> activeConnections = new ConcurrentHashMap<>();

    // Adds a new connection to the hashmap
    public boolean addConnection(String userID, WebSocketSession session) {
        // Check that we have a user ID
        if (userID == null || userID.trim().isEmpty()) {
            logger.warn("Cannot add connection: userID is null or empty");
            return false;
        }

        // Check if the session is null
        if (session == null) {
            logger.warn("Cannot add connection: Session is null for userID: {}", userID);
            return false;
        }

        // Check if the session is open
        if (!session.isOpen()) {
            logger.warn("Cannot add connection: session is not open for userID: {}", userID);
            return false;
        }

        // Remove any potentially lingering connections for the  (reconnection scenarios)
        WebSocketSession existingSession = activeConnections.get(userID);
        if (existingSession != null && existingSession.isOpen()) {
            logger.info("Replacing existing connection for userID: {}", userID);
            try {
                existingSession.close();
            } catch (Exception e) {
                logger.error("Error closing existing session for userID: {}", userID, e);
            }
        }

        // Create entry in the hashmap for new connection
        activeConnections.put(userID, session);
        logger.info("Added connection for userID: {}. Total active connections: {}", userID, activeConnections.size());
        return true;
    }

    // Removes connection from the hashmap
    public boolean removeConnection(String userID) {
        // Check that we have a user ID
        if (userID == null || userID.trim().isEmpty()) {
            logger.warn("Cannot remove connection: userID is null or empty");
            return false;
        }

        // Remove the session
        WebSocketSession removedSession = activeConnections.remove(userID);

        // Create success/failure log
        if (removedSession != null) {
            logger.info("Removed connection for userID: {}. Total active connections: {}",
                    userID, activeConnections.size());
        } else {
            logger.debug("No connection found to remove for userID: {}", userID);
        }

        return true;
    }

    // Retrieves the WebSocket connection for a given user
    public WebSocketSession getConnection(String userID) {
        // Check that we have a user ID
        if (userID == null || userID.trim().isEmpty()) {
            logger.warn("Cannot retrieve connection: userID is null or empty");
            return null;
        }

        WebSocketSession session = activeConnections.get(userID);

        // Since we have the session, we just check if it is inactive
        if (session != null && !session.isOpen()) {
            logger.debug("Removing closed session for userID: {}", userID);
            // Remove closed session from map
            activeConnections.remove(userID);
            return null;
        }

        return session;
    }

    // Determines if user is online (if they have an active websocket connection
    public boolean isOnline(String userID) {
        // Check that we have a user ID
        if (userID == null || userID.trim().isEmpty()){
            return false;
        }

        // Retrieve session
        WebSocketSession session = activeConnections.get(userID);
        // Check session is active and open
        boolean isOnline = session != null && session.isOpen();

        // Perform cleanup if session is closed
        if (session != null && !session.isOpen()){
            logger.debug("Cleaning up closed session for userID: {}", userID);
            activeConnections.remove(userID);
            isOnline = false;
        }

        return isOnline;
    }

    // Returns the number of active connections in the hashmap
    public int getActiveConnCount() {
        cleanupClosedSessions();
        return activeConnections.size();
    }

    public Map<String, WebSocketSession> getAllConnections(){
        cleanupClosedSessions();
        return new ConcurrentHashMap<>(activeConnections);
    }

    // Returns all currently online user IDs
    public Set<String> getOnlineUsers() {
        cleanupClosedSessions();
        return new HashSet<>(activeConnections.keySet());
    }

    // Gets the userID associated with a given WebSocket session
    public String GetUserIdBySession(WebSocketSession session) {
        if (session == null) {return null;}

        // Iterate over connections and return user ID associated with session if it exists
        for (Map.Entry<String, WebSocketSession> entry : activeConnections.entrySet()){
            if (entry.getValue().equals(session)) {
                return entry.getKey();
            }
        }

        return null;
    }

    // Terminates all connections and clears the hashmap
    public void clearAllConns() {
        logger.info("Clearing all connections. Current count: {}", activeConnections.size());

        // Iterate over hashmap and close all active connections
        for (Map.Entry<String, WebSocketSession> entry : activeConnections.entrySet()) {
            try {
                WebSocketSession session = entry.getValue();
                if (session.isOpen()){
                    session.close();
                }
            } catch (Exception e){
                logger.error("Error closing session for userID: {}", entry.getKey(), e);
            }
        }

        // Clear the hashmap
        activeConnections.clear();
        logger.info("All connections cleared");
    }

    // Performs cleanup of closed sessions
    public void cleanupClosedSessions() {
        activeConnections.entrySet().removeIf(entry -> {
            boolean shouldRemove = !entry.getValue().isOpen();
            if (shouldRemove) {
                logger.debug("Cleaning up closed session for userID: {}", entry.getKey());
            }
            return shouldRemove;
        });
    }
}
