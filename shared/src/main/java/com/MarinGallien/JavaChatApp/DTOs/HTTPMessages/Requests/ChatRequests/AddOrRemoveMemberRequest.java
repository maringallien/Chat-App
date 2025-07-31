package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.ChatRequests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record AddOrRemoveMemberRequest(
        @NotBlank(message = "Creator ID is required")
        String creatorId,

        @NotBlank(message = "User ID is required")
        String memberId,

        @NotBlank(message = "Chat ID is required")
        String chatId
) implements ApiReqResInterface {}