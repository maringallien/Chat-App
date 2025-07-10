package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.ApiReqResInterface;

public record GenericResponse(
        boolean success,
        String message
) implements ApiReqResInterface {}