package DTOs.HTTPMessages.Requests.ChatRequests;

import DTOs.HTTPMessages.Requests.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record AddOrRemoveMemberRequest(
        @NotBlank(message = "Creator ID is required")
        String creatorId,

        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Chat ID is required")
        String chatId
) implements ApiReqResInterface {}