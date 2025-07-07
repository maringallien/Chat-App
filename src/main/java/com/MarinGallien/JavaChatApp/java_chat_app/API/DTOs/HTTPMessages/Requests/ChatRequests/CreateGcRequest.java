package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ApiRequest;
import jakarta.validation.constraints.*;
import java.util.Set;

public record CreateGcRequest(
        @NotBlank(message = "Creator ID is required")
        String creatorId,

        @NotEmpty(message = "Member list cannot be empty")
        @Size(min = 1, max = 50, message = "Group chat must have 1-50 members")
        Set<String> memberIds,

        @NotBlank(message = "Chat name is required")
        @Size(max = 100, message = "Chat name cannot exceed 100 characters")
        String chatName
) implements ApiRequest {}