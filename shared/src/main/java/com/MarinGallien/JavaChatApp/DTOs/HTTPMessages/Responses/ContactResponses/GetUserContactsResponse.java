package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.ContactResponses;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.UserDTO;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;

public record GetUserContactsResponse(
        boolean success,
        String message,
        List<UserDTO> contacts
) implements ApiReqResInterface {}
