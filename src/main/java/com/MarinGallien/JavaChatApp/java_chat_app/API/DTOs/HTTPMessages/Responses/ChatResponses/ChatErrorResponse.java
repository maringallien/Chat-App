package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ChatResponses;


import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ApiResponse;

import java.util.List;

public record ChatErrorResponse(
        boolean success,
        String message,
        String errorCode
) implements ApiResponse {

    // Convenience constructor for error
    public ChatErrorResponse(String message, String errorCode) {
        this(false, message, errorCode);
    }
}