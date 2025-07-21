package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record GetUserChatsRequest(
        @NotBlank(message = "User ID is required")
        String userId
) implements ApiReqResInterface {}
