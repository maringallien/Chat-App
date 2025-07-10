package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record CreatePcRequest(
        @NotBlank(message = "First user ID is required")
        String userId1,

        @NotBlank(message = "Second user ID is required")
        String userId2
) implements ApiReqResInterface {}