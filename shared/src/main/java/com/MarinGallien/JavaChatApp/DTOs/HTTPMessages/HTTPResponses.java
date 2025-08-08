package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.FileDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;


// Consolidated file containing all HTTPMessages Response records.
// Organized by category using nested static classes.

public class HTTPResponses {

    // ========== Generic response used across multiple endpoints ==========
    public static record GenericResponse(
            boolean success,
            String message
    ) implements ApiReqResInterface {}


    // ========== Authentication-related responses ==========
    public static record LoginResponse(
            boolean success,
            String message,
            String userId,
            String username,
            String JwtToken
    ) implements ApiReqResInterface {}


    // ========== Chat-related responses ==========
    public static record GetUserChatsResponse(
            boolean success,
            String message,
            List<ChatDTO> chats
    ) implements ApiReqResInterface {}


    // ========== Contact-related responses ==========
    public static record GetUserContactsResponse(
            boolean success,
            String message,
            List<ContactDTO> contacts
    ) implements ApiReqResInterface {}


    // ========== File-related responses ==========
    public static record GetChatFilesResponse(
            boolean success,
            String message,
            List<FileDTO> files
    ) implements ApiReqResInterface {}


    // ========== Message-related responses ==========
    public static record GetChatMessagesResponse(
            boolean success,
            String message,
            List<MessageDTO> messages
    ) implements ApiReqResInterface {}

}