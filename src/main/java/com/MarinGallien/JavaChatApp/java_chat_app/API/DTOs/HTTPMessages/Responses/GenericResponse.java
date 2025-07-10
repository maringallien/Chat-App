package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses;

public record GenericResponse(
        boolean success,
        String message
) implements ApiResponse {}