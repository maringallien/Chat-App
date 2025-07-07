package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses;

public interface ApiResponse {
    // Marker interface for all API responses
    default long timestamp() {
        return java.time.Instant.now().toEpochMilli();
    }
}