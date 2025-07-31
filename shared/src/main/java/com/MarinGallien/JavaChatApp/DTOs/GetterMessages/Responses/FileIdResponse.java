package com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

public record FileIdResponse(
        boolean success,
        String fileId
) implements ApiReqResInterface {}
