package com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserIdsRequest(
    @NotBlank
    List<String> usernames
) implements ApiReqResInterface {}
