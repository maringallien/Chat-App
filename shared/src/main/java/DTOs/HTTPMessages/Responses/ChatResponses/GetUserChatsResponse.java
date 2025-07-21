package DTOs.HTTPMessages.Responses.ChatResponses;

import DTOs.DataEntities.ChatDTO;
import DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;

public record GetUserChatsResponse(
        boolean success,
        String message,
        List<ChatDTO> chats
) implements ApiReqResInterface {}