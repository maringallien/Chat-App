package DTOs.HTTPMessages.Requests.FileRequests;

import DTOs.HTTPMessages.Requests.ApiReqResInterface;
import jakarta.validation.constraints.NotBlank;

public record DownloadFileRequest(
        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Chat ID is required")
        String chatId,

        @NotBlank(message = "file ID is required")
        String fileId
) implements ApiReqResInterface {}
