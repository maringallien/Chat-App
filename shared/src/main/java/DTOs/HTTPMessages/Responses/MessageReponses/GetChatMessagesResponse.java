package DTOs.HTTPMessages.Responses.MessageReponses;

import DTOs.DataEntities.MessageDTO;
import DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;

public record GetChatMessagesResponse(
    boolean success,
    String message,
    List<MessageDTO> messages
) implements ApiReqResInterface {}
