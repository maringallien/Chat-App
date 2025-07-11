package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.FileRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record DeleteFileRequest(
        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Chat ID is required")
        String chatId,

        @NotBlank(message = "file ID is required")
        String fileId
) implements ApiReqResInterface {}
