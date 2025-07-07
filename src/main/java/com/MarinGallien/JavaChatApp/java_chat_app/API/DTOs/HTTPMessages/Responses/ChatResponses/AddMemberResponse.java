package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ChatResponses;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ApiResponse;

public record AddMemberResponse(
        boolean success,
        String message
) implements ApiResponse {}