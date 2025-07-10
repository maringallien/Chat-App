package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ContactRequests;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record GetUserContactsRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId
) implements ApiReqResInterface {
}
