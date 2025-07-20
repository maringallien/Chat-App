package DTOs.HTTPMessages.Responses.ContactResponses;

import DTOs.HTTPMessages.ApiReqResInterface;
import Database.JPAEntities.CoreEntities.User;

import java.util.List;

public record GetUserContactsResponse(
        boolean success,
        String message,
        List<User> contacts
) implements ApiReqResInterface {}
