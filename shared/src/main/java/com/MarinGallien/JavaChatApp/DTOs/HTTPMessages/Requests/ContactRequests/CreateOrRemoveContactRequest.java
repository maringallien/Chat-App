package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.ContactRequests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record CreateOrRemoveContactRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Contact ID cannot be blank")
        String contactId
) implements ApiReqResInterface {
}
