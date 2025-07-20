package DTOs.HTTPMessages.Requests.UserRequests;

import DTOs.HTTPMessages.Requests.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record UpdatePasswdRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Old password cannot be blank")
        String oldPassword,

        @NotBlank(message = "New password cannot be blank")
        String newPassword
) implements ApiReqResInterface {}
