package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.ChatResponses;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;

public record GetUserChatsResponse(
        boolean success,
        String message,
        List<ChatDTO> chats
) implements ApiReqResInterface {}