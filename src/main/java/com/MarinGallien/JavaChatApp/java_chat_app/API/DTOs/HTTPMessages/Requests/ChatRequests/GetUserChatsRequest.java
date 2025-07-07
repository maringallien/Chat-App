package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ApiRequest;

import jakarta.validation.constraints.NotBlank;

public record GetUserChatsRequest(
        @NotBlank(message = "User ID is required")
        String userId
) implements ApiRequest {}
