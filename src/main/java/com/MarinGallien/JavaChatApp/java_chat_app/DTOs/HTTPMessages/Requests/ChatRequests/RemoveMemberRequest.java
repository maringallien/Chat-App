package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.ApiRequest;
import jakarta.validation.constraints.NotBlank;

public record RemoveMemberRequest(
        @NotBlank(message = "Creator ID is required")
        String creatorId,

        @NotBlank(message = "User ID to remove is required")
        String userId,

        @NotBlank(message = "Chat ID is required")
        String chatId
) implements ApiRequest {}