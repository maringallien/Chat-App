package DTOs.HTTPMessages.Requests.UserRequests;

import DTOs.HTTPMessages.Requests.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record UpdateUnameRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Username cannot be blank")
        String username
) implements ApiReqResInterface {}
