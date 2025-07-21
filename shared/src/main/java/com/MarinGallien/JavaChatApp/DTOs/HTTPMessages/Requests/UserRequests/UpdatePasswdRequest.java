package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.UserRequests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record UpdatePasswdRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Old password cannot be blank")
        String oldPassword,

        @NotBlank(message = "New password cannot be blank")
        String newPassword
) implements ApiReqResInterface {}
