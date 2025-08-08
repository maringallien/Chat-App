package com.MarinGallien.JavaChatApp.DTOs.GetterMessages;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;


// Container class for all GetterMessages Request records.
// Uses nested static records to keep them in one file.

public class GetterRequests {

    public static record ChatIdRequest(
            @NotBlank(message = "Chat name cannot be blank")
            String chatName
    ) implements ApiReqResInterface {}

    public static record FileIdRequest(
            @NotBlank(message = "File name cannot be blank")
            String filename,

            @NotBlank(message = "Chat ID cannot be blank")
            String chatId
    ) implements ApiReqResInterface {}

    public static record UserIdRequest(
            @NotBlank(message = "username cannot be blank")
            String username
    ) implements ApiReqResInterface {}

    public static record UserIdsRequest(
            @NotNull(message = "Usernames list cannot be null")
            @NotEmpty(message = "Usernames list cannot be empty")
            List<String> usernames
    ) implements ApiReqResInterface {}
}