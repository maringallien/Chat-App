package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    // Parameters
    @NotBlank(message = "Room ID cannot be blank")
    private final String roomID;

    @NotBlank(message = "Creation timestamp cannot be blank")
    private Long createdAt;

    private final Set<String> participants = ConcurrentHashMap.newKeySet();

    // Constructor
    public Room(String roomID) {
        this.roomID = roomID;
        this.createdAt = Instant.now().toEpochMilli();
    }

    // Method adds new participant to room
    private boolean adMember(String userID) {

    }

    // Method removes member from room
    private boolean removeMember(String userID) {

    }

    // Method checks if the room contains userID
    private boolean hasMember(String userID) {

    }

    // Method returns the participants of the room
    private Set<String> getParticipants() {

    }

    // Removes participants from room
    private boolean clearRoom() {

    }
}
