package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ChatResponses;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ApiResponse;

import java.util.List;

public record GetUserChatsResponse(
        boolean success,
        String message,
        String userId,
        List<ChatSummary> chats,
        int totalChats
) implements ApiResponse {}