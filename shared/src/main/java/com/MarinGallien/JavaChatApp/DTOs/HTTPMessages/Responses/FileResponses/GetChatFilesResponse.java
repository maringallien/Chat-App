package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.FileResponses;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.FileDTO;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;


public record GetChatFilesResponse(
        boolean success,
        String message,
        List<FileDTO> files
) implements ApiReqResInterface {}
