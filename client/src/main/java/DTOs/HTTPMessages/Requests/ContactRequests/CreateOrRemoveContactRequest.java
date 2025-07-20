package DTOs.HTTPMessages.Requests.ContactRequests;

import DTOs.HTTPMessages.Requests.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record CreateOrRemoveContactRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Contact ID cannot be blank")
        String contactId
) implements ApiReqResInterface {
}
