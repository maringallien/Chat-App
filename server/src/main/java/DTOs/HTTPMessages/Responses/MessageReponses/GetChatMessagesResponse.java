package DTOs.HTTPMessages.Responses.MessageReponses;

import DTOs.HTTPMessages.ApiReqResInterface;
import Database.JPAEntities.CoreEntities.Message;

import java.util.List;

public record GetChatMessagesResponse(
    boolean success,
    String message,
    List<Message> messages
) implements ApiReqResInterface {}
