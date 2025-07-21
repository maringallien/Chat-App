package DTOs.HTTPMessages.Responses.FileResponses;

import DTOs.DataEntities.FileDTO;
import DTOs.HTTPMessages.ApiReqResInterface;

import java.util.List;


public record GetChatFilesResponse(
        boolean success,
        String message,
        List<FileDTO> files
) implements ApiReqResInterface {}
