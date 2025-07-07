package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ChatResponses;

import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;

import java.util.Set;

public record ChatSummary(
        String chatId,
        ChatType chatType,
        String chatName,
        String creatorId,
        Set<String> participantIds,
        int memberCount,
        long createdAt
) {}
