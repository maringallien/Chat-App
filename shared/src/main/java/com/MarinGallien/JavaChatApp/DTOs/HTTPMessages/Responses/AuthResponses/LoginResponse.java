package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.AuthResponses;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

public record LoginResponse(
    boolean success,
    String message,
    String userId,
    String JwtToken
) implements ApiReqResInterface {}
