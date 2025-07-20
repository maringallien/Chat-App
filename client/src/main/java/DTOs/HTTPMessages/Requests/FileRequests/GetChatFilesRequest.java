package DTOs.HTTPMessages.Requests.FileRequests;

import DTOs.HTTPMessages.Requests.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record GetChatFilesRequest (
        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Chat ID is required")
        String chatId
) implements ApiReqResInterface {}
