package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Responses;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.ApiReqResInterface;

public record GenericResponse(
        boolean success,
        String message
) implements ApiReqResInterface {}