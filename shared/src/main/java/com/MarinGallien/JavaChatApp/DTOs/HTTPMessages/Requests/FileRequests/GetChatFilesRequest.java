package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.FileRequests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record GetChatFilesRequest (
        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Chat ID is required")
        String chatId
) implements ApiReqResInterface {}
