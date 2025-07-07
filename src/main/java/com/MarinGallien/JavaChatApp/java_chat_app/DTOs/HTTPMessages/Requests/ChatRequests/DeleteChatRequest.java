package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.ApiRequest;
import jakarta.validation.constraints.NotBlank;

public record DeleteChatRequest(
        @NotBlank(message = "Creator ID is required")
        String creatorId,

        @NotBlank(message = "Chat ID is required")
        String chatId
) implements ApiRequest {}