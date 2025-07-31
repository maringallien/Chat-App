package com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

public record UserIdResponse(
        boolean success,
        String userId
) implements ApiReqResInterface {}
