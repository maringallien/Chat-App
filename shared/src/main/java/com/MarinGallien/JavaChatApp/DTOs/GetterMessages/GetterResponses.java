package com.MarinGallien.JavaChatApp.DTOs.GetterMessages;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;

// Container class for all GetterMessages Response records.
// Uses nested static records to keep them in one file.

public class GetterResponses {

    public static record ChatIdResponse(
            boolean success,
            String chatId
    ) implements ApiReqResInterface {}

    public static record FileIdResponse(
            boolean success,
            String fileId
    ) implements ApiReqResInterface {}

    public static record UserIdResponse(
            boolean success,
            String userId
    ) implements ApiReqResInterface {}

    public static record UserIdsResponse(
            boolean success,
            List<String> userIds
    ) implements ApiReqResInterface {}
}