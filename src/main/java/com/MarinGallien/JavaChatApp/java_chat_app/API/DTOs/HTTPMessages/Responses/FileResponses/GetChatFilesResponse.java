package com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.FileResponses;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.ApiReqResInterface;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.File;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


public record GetChatFilesResponse(
        boolean success,
        String message,
        List<File> files
) implements ApiReqResInterface {}
