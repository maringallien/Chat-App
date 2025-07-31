package com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record FileIdRequest(
        @NotBlank(message = "File name cannot be blank")
        String filename
) implements ApiReqResInterface {}
