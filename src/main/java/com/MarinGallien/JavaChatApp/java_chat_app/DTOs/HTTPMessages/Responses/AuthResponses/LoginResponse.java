package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Responses.AuthResponses;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.ApiReqResInterface;

public record LoginResponse(
    boolean success,
    String message,
    String userId,
    String JwtToken
) implements ApiReqResInterface {}
