package DTOs.HTTPMessages.Requests.UserRequests;

import DTOs.HTTPMessages.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Email cannot be blank")
        String email
) implements ApiReqResInterface {}
