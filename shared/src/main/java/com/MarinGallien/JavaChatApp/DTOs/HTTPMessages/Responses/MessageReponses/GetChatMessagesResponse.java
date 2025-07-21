package com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.MessageReponses;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;

public record GetChatMessagesResponse(
    boolean success,
    String message,
    List<MessageDTO> messages
) implements ApiReqResInterface {}
