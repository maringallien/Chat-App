package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.MessageReponses;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.ApiReqResInterface;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;

import java.util.List;

public record GetChatMessagesResponse(
    boolean success,
    String message,
    List<Message> messages
) implements ApiReqResInterface {}
