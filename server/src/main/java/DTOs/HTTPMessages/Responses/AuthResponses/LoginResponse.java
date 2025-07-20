package DTOs.HTTPMessages.Responses.AuthResponses;

import DTOs.HTTPMessages.ApiReqResInterface;

public record LoginResponse(
    boolean success,
    String message,
    String userId,
    String JwtToken
) implements ApiReqResInterface {}
