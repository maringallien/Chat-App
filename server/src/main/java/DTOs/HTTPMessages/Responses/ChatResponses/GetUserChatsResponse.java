package DTOs.HTTPMessages.Responses.ChatResponses;

import DTOs.HTTPMessages.ApiReqResInterface;
import Database.JPAEntities.CoreEntities.Chat;

import java.util.List;

public record GetUserChatsResponse(
        boolean success,
        String message,
        List<Chat> chats
) implements ApiReqResInterface {}