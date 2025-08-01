package com.MarinGallien.JavaChatApp.Controllers;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.FileDTO;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests.ChatIdRequest;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests.FileIdRequest;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses.ChatIdResponse;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses.FileIdResponse;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.FileRequests.DeleteFileRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.FileRequests.DownloadFileRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.FileRequests.GetChatFilesRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.FileResponses.GetChatFilesResponse;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.GenericResponse;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.File;
import com.MarinGallien.JavaChatApp.Services.FileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<GenericResponse> uploadFile(
            @RequestParam("userId") String userId,
            @RequestParam("chatId") String chatId,
            @RequestParam("file")MultipartFile file) {

        try {
            // Validate IDs
            if (!validateId(userId) || !validateId(chatId)) {
                logger.warn("Failed to upload file: user or chat ID null or empty");
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Input parameter null or empty"));
            }

            // Validate file
            if (file == null || file.isEmpty()) {
                logger.warn("Failed to upload file: File null or empty");
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "File is null or empty"));
            }

            // Delegate to FileService
            File uploadedFile = fileService.uploadFile(userId, chatId, file);

            if (uploadedFile == null) {
                logger.warn("Failed to upload file");
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Failed to upload file"));
            }

                logger.info("Successfully uploaded file");
                return ResponseEntity.ok().body(new GenericResponse(true, "File uploaded successfully"));

        } catch (Exception e) {
            logger.error("Failed to upload file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Failed to upload file"));
        }
    }

    @PostMapping("/download")
    public ResponseEntity<?> downloadFile (
            @Valid @RequestBody DownloadFileRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has errors
            if (bindingResult.hasErrors()) {
                logger.warn("Failed to download file: input has error(s)");
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Failed to download file: Input has errors"));
            }

            // Delegate to FileService
            Resource resource = fileService.downloadFile(request.userId(), request.chatId(), request.fileId());

            // If resource is null, send error message
            if (resource == null) {
                logger.warn("Failed to download file");
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Failed to download file"));
            }

            logger.info("Sending file to client");
            return ResponseEntity.ok(resource); // Spring handles file serving

        } catch (Exception e) {
            logger.error("Failed to download file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Failed to download file"));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<GenericResponse> deleteFile(
            @Valid @RequestBody DeleteFileRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has errors
            if (bindingResult.hasErrors()) {
                logger.warn("Failed to delete file: input has error(s)");
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Failed to delete file: Input has errors"));
            }

            // Delegate to FileService
            boolean deleted = fileService.deleteFile(request.userId(), request.chatId(), request.fileId());

            if (!deleted) {
                logger.warn("Failed to delete file");
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Failed to delete file"));
            }

            logger.info("Successfully deleted file");
            return ResponseEntity.ok().body(new GenericResponse(true, "Successfully deleted file"));

        } catch (Exception e) {
            logger.error("Failed to delete file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Failed to delete file"));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<GetChatFilesResponse> getChatFiles (
            @Valid @RequestBody GetChatFilesRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has errors
            if (bindingResult.hasErrors()) {
                logger.warn("Failed to retrieve chat files: input has error(s)");
                return ResponseEntity.badRequest()
                        .body(new GetChatFilesResponse(false, "Failed to retrieve files: Input has errors", List.of()));
            }

            // Delegate to FileService
            List<FileDTO> chatFiles = fileService.getChatFiles(request.userId(), request.chatId());

            // Check if retrieval was successful
            if (chatFiles == null) {
                logger.error("Failed to retrieve chat files");
                return ResponseEntity.badRequest()
                        .body(new GetChatFilesResponse(false, "Failed to retrieve chat files", List.of()));
            }

            // Send success response
            logger.info("Successfully retrieved chat files");
            return ResponseEntity.ok()
                    .body(new GetChatFilesResponse(true, "Chat files retrieved successfully", chatFiles));

        } catch (Exception e) {
            logger.error("Failed to get chat files");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GetChatFilesResponse(false, "Failed to get chat files", List.of()));
        }
    }

    @PostMapping("/fileId")
    public ResponseEntity<FileIdResponse> getFileId(
            @Valid @RequestBody FileIdRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has any errors
            if (bindingResult.hasErrors()) {
                logger.warn("Invalid request to retrieve chat ID from chat name: input parameters null or empty");
                return ResponseEntity.badRequest().body(new FileIdResponse(false, null));
            }

            // Delegate to chat service
            String fileId = fileService.getFileIdFromFilename(request.filename());

            // Handle no corresponding ID
            if (fileId == null || fileId.isEmpty()) {
                logger.warn("No ID found");
                return ResponseEntity.badRequest().body(new FileIdResponse(false, null));
            }

            logger.info("Sending back chat Id");
            return ResponseEntity.ok().body(new FileIdResponse(true, fileId));
        } catch (Exception e) {
            logger.error("Failed to retrieve chat Id");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FileIdResponse(false, null));
        }
    }
    private boolean validateId(String id) {
        return id != null && !id.trim().isEmpty();
    }
}
