package DTOs.HTTPMessages.Responses;

import DTOs.HTTPMessages.ApiReqResInterface;

public record GenericResponse(
        boolean success,
        String message
) implements ApiReqResInterface {}