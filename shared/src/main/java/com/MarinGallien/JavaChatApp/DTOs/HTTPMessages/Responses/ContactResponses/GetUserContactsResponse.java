package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.ContactResponses;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;

public record GetUserContactsResponse(
        boolean success,
        String message,
        List<ContactDTO> contacts
) implements ApiReqResInterface {}
