package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages;

public record AuthResponseMessage (
    String token,
    String userId,
    String username,
    String email,
    String message,
    boolean success

) {
    // Constructor for successful authentication
    public AuthResponseMessage(String token, String userId, String username, String email) {
        this(token, userId, username, email, "Authentication successful", true);
    }

    // Constructor for failed authentication
    public AuthResponseMessage(String message) {
        this(null, null, null, null, message, false);
    }
}
