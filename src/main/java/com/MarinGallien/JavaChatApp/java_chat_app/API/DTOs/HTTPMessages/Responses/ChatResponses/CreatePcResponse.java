package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ChatResponses;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ApiResponse;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;

public record CreatePcResponse(
        boolean success,
        String message
) implements ApiResponse {}