package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Responses.ChatResponses;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.ApiReqResInterface;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;

import java.util.List;

public record GetUserChatsResponse(
        boolean success,
        String message,
        List<Chat> chats
) implements ApiReqResInterface {}