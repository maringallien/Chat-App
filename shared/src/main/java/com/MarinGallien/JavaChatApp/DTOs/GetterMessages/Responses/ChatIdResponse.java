package com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

public record ChatIdResponse (
        boolean success,
        String chatId
) implements ApiReqResInterface {}
