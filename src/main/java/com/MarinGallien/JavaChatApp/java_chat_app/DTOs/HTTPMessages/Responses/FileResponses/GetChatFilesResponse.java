package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Responses.FileResponses;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.ApiReqResInterface;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.File;

import java.util.List;


public record GetChatFilesResponse(
        boolean success,
        String message,
        List<File> files
) implements ApiReqResInterface {}
