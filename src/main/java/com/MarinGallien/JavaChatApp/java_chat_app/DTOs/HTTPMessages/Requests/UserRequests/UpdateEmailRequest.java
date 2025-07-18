package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.UserRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Email cannot be blank")
        String email
) implements ApiReqResInterface {}
