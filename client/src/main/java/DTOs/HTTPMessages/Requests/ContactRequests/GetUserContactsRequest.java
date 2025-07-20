package DTOs.HTTPMessages.Requests.ContactRequests;

import DTOs.HTTPMessages.Requests.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record GetUserContactsRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId
) implements ApiReqResInterface {
}
