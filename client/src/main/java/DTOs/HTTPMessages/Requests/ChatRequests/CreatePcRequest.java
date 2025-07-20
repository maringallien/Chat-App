package DTOs.HTTPMessages.Requests.ChatRequests;

import DTOs.HTTPMessages.Requests.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record CreatePcRequest(
        @NotBlank(message = "First user ID is required")
        String userId1,

        @NotBlank(message = "Second user ID is required")
        String userId2
) implements ApiReqResInterface {}