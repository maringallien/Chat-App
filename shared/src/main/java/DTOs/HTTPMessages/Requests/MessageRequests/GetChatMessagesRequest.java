package DTOs.HTTPMessages.Requests.MessageRequests;

import DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record GetChatMessagesRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Chat ID cannot be blank")
        String chatId
) implements ApiReqResInterface {}
