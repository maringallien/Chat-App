package DTOs.HTTPMessages.Requests.AuthRequests;

import DTOs.HTTPMessages.Requests.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Password cannot be blank")
        String password
) implements ApiReqResInterface {}
