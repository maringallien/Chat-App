package DTOs.HTTPMessages.Responses.FileResponses;

import DTOs.HTTPMessages.ApiReqResInterface;
import Database.JPAEntities.CoreEntities.File;

import java.util.List;


public record GetChatFilesResponse(
        boolean success,
        String message,
        List<File> files
) implements ApiReqResInterface {}
