package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ApiRequest;
import jakarta.validation.constraints.NotBlank;

public record AddOrRemoveMemberRequest(
        @NotBlank(message = "Creator ID is required")
        String creatorId,

        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Chat ID is required")
        String chatId
) implements ApiRequest {}