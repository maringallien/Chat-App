package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record GetUserChatsRequest(
        @NotBlank(message = "User ID is required")
        String userId
) implements ApiReqResInterface {}
