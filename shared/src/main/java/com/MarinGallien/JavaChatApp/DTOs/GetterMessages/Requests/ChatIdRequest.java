package com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record ChatIdRequest(
        @NotBlank(message = "Chat name cannot be blank")
        String chatName
) implements ApiReqResInterface {}
