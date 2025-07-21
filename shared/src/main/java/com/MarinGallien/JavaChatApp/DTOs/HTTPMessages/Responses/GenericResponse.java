package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

public record GenericResponse(
        boolean success,
        String message
) implements ApiReqResInterface {}