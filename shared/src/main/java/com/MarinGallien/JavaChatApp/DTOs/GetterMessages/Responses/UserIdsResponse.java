package com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;

public record UserIdsResponse(
        boolean success,
        List<String> userIds
) implements ApiReqResInterface {}
