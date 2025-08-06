package com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserIdsRequest(
    @NotNull(message = "Usernames list cannot be null")
    @NotEmpty(message = "Usernames list cannot be empty")
    List<String> usernames
) implements ApiReqResInterface {}
