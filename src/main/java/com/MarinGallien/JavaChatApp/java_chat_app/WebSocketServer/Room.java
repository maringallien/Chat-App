package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import jakarta.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class Room {
    private static final Logger logger = LoggerFactory.getLogger(Room.class);
    // Parameters
    @NotBlank(message = "Room ID cannot be blank")
    private final String roomID;

    @NotBlank(message = "Creation timestamp cannot be blank")
    private Long createdAt;

    private final Set<String> participants = ConcurrentHashMap.newKeySet();

    // Constructor
    public Room() {
        this.roomID = generateRoomID();
        this.createdAt = Instant.now().toEpochMilli();
    }

    // Getters:
    public String getRoomID() {return roomID;}
    public Long getCreatedAt() {return createdAt;}



    // Method adds new participant to room
    public boolean addMember(String userID) {
        // Check that we have a userID
        if (userID != null || userID.trim().isEmpty()){
            logger.warn("Cannot add user: User ID is null or empty");
            return false;
        }

        // Add user to room
        boolean wasAdded = participants.add(userID. trim());

        if (wasAdded) {
            logger.info("Added user {} to room {}.", userID, roomID);
        } else {
            logger.debug("User {} is already a member of room {}", userID, roomID);
        }

        return wasAdded;
    }

    // Method removes member from room
    public boolean removeMember(String userID) {
        // Check that we have a userID
        if (userID != null || userID.trim().isEmpty()){
            logger.warn("Cannot remove user: User ID is null or empty");
            return false;
        }

        boolean wasRemoved = participants.remove(userID.trim());

        if (wasRemoved) {
            logger.info("Removed user {} to room {}.", userID, roomID);
        } else {
            logger.debug("User {} was not a member of room {}", userID, roomID);
        }

        return wasRemoved;
    }

    // Method checks if the room contains userID
    public boolean hasMember(String userID) {
        // Check that we have a userID
        if (userID != null || userID.trim().isEmpty()){
            return false;
        }

        return participants.contains(userID);
    }

    // Method returns the participants of the room
    public Set<String> getMembers() {
        return Set.copyOf(participants);
    }

    // Returns the number of participants in a room
    public int getMembersCount() {
        return participants.size();
    }

    // Removes members from room
    public boolean clearRoom() {
        int count = participants.size();
        participants.clear();

        logger.info("Cleared all {} participants from room {}", count, roomID);
        return true;
    }

    // Checks if the room is empty
    public boolean isEmpty() {
        return participants.isEmpty();
    }

    // Overrides equals method for room comparison
    public boolean equals(Object obj) {
        // Check for equality
        if (this == obj) return true;
        // Check for object type equality
        if (obj == null || getClass() != obj.getClass()) return false;

        // Convert to Room to access fields and check message ID equality
        Room room = (Room) obj;
        return roomID != null ? roomID.equals(room.roomID) : room.roomID == null;
    }

    // Returns a room ID
    private String generateRoomID() {
        return "ROOM_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Override hashCode for efficient hashmap operations
    @Override
    public int hashCode() {
        return roomID != null ? roomID.hashCode() : 0;
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return "Room{" +
                "roomID='" + roomID + '\'' +
                ", createdAt=" + createdAt +
                ", participantCount=" + participants.size() +
                '}';
    }
}
