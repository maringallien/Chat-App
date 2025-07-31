package com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record UserIdRequest(
        @NotBlank(message = "username cannot be blank")
        String username
) implements ApiReqResInterface {}
