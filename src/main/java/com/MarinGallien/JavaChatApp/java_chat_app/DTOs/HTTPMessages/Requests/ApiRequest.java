package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests;


public interface ApiRequest {
    // Marker interface for all API requests
    default String requestId() {
        return java.util.UUID.randomUUID().toString();
    }

    default long timestamp() {
        return java.time.Instant.now().toEpochMilli();
    }
}