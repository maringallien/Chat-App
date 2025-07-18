package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.MessageRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record GetChatMessagesRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Chat ID cannot be blank")
        String chatId
) implements ApiReqResInterface {}
