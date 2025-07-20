package DTOs.HTTPMessages.Requests.ChatRequests;

import DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record DeleteChatRequest(
        @NotBlank(message = "Creator ID is required")
        String creatorId,

        @NotBlank(message = "Chat ID is required")
        String chatId
) implements ApiReqResInterface {}