package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ContactResponses;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.ApiReqResInterface;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;

import java.util.List;

public record GetUserContactsResponse(
        boolean success,
        String message,
        List<User> contacts
) implements ApiReqResInterface {}
