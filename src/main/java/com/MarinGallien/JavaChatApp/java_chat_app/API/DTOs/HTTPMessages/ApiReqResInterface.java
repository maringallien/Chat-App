package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages;

public interface ApiReqResInterface {
    // Marker interface for all API responses
    default long timestamp() {
        return java.time.Instant.now().toEpochMilli();
    }
}