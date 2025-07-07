package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Responses;

interface ApiResponse {
    // Marker interface for all API responses
    default long timestamp() {
        return java.time.Instant.now().toEpochMilli();
    }
}