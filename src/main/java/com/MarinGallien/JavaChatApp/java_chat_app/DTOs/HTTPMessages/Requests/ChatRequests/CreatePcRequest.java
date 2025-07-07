package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.ApiRequest;
import jakarta.validation.constraints.NotBlank;

public record CreatePcRequest(
        @NotBlank(message = "First user ID is required")
        String userId1,

        @NotBlank(message = "Second user ID is required")
        String userId2
) implements ApiRequest {}