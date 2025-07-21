package com.MarinGallien.JavaChatApp.WebSocketServer;

import jakarta.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class Chat {
    private static final Logger logger = LoggerFactory.getLogger(Chat.class);
    // Parameters
    @NotBlank(message = "Chat ID cannot be blank")
    private final String chatId;

    @NotBlank(message = "Creation timestamp cannot be blank")
    private Long createdAt;

    private final Set<String> participants = ConcurrentHashMap.newKeySet();

    // Default constructor generates chatId internally
    public Chat() {
        this.chatId = generateChatId();
        this.createdAt = Instant.now().toEpochMilli();
    }

    public Chat(String chatId) {
        this.chatId = chatId;
        this.createdAt = Instant.now().toEpochMilli();
    }

    // Getters:
    public String getChatId() {return chatId;}
    public Long getCreatedAt() {return createdAt;}


    // Check user ID is valid
    private boolean checkId(String ID) {
       return ID != null && !ID.trim().isEmpty();
    }

    // Method adds new participant to chat
    public boolean addMember(String userID) {
        // Check that we have a userID
        if (!checkId(userID)){
            logger.warn("Cannot add user: User ID is null or empty");
            return false;
        }

        // Add user to chat
        boolean wasAdded = participants.add(userID.trim());

        if (wasAdded) {
            logger.info("Added user {} to chat {}.", userID, chatId);
        } else {
            logger.debug("User {} is already a member of chat {}", userID, chatId);
        }

        return wasAdded;
    }

    // Method removes member from chat
    public boolean removeMember(String userID) {
        // Check that we have a userID
        if (!checkId(userID)){
            logger.warn("Cannot remove user: User ID is null or empty");
            return false;
        }

        boolean wasRemoved = participants.remove(userID.trim());

        if (wasRemoved) {
            logger.info("Removed user {} to chat {}.", userID, chatId);
        } else {
            logger.debug("User {} was not a member of chat {}", userID, chatId);
        }

        return wasRemoved;
    }

    // Method checks if the chat contains userID
    public boolean hasMember(String userID) {
        // Check that we have a userID
        if (!checkId(userID)){
            return false;
        }

        return participants.contains(userID);
    }

    // Method returns the participants of the chat
    public Set<String> getMembers() {
        return Set.copyOf(participants);
    }

    // Returns the number of participants in a chat
    public int getMembersCount() {
        return participants.size();
    }

    // Removes members from chat
    public boolean clearChat() {
        int count = participants.size();
        participants.clear();

        logger.info("Cleared all {} participants from chat {}", count, chatId);
        return true;
    }

    // Checks if the chat is empty
    public boolean isEmpty() {
        return participants.isEmpty();
    }

    // Overrides equals method for chat comparison
    public boolean equals(Object obj) {
        // Check for equality
        if (this == obj) return true;
        // Check for object type equality
        if (obj == null || getClass() != obj.getClass()) return false;

        // Convert to chat to access fields and check message ID equality
        Chat chat = (Chat) obj;
        return chatId != null ? chatId.equals(chat.chatId) : chat.chatId == null;
    }

    // Returns a chat ID
    private String generateChatId() {
        return "chat_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Override hashCode for efficient hashmap operations
    @Override
    public int hashCode() {
        return chatId != null ? chatId.hashCode() : 0;
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return "chat{" +
                "chatId='" + chatId + '\'' +
                ", createdAt=" + createdAt +
                ", participantCount=" + participants.size() +
                '}';
    }
}
