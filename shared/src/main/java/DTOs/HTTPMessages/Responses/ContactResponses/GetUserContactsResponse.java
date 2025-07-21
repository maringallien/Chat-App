package DTOs.HTTPMessages.Responses.ContactResponses;

import DTOs.DataEntities.UserDTO;
import DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;

public record GetUserContactsResponse(
        boolean success,
        String message,
        List<UserDTO> contacts
) implements ApiReqResInterface {}
