package com.MarinGallien.JavaChatApp.Services;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.FileDTO;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.FileDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.File;
import com.MarinGallien.JavaChatApp.Database.Mappers.FileMapper;
import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService {

    private static Logger logger = LoggerFactory.getLogger(FileService.class);

    private final FileDbService fileDbService;
    private final FileMapper fileMapper;

    public FileService(FileDbService fileDbService, FileMapper fileMapper) {
        this.fileDbService = fileDbService;
        this.fileMapper = fileMapper;
    }

    public File uploadFile(String userId, String chatId, MultipartFile file) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(chatId)) {
                logger.warn("Failed to upload file: user or chat ID is null or empty");
                return null;
            }

            if (file == null || file.isEmpty()) {
                logger.warn("Failed to upload file: file is null or empty");
                return null;
            }

            File uploadedFile = fileDbService.uploadFile(userId, chatId, file);

            if (uploadedFile == null) {
                logger.warn("Failed to upload file");
                return null;
            }

            logger.info("Successfully uploaded file");
            return uploadedFile;

        } catch (Exception e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            return null;
        }
    }

    public Resource downloadFile(String userId, String chatId, String fileId) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(fileId) || !validateId(chatId)) {
                logger.warn("Failed to download file: user or file ID is null or empty");
                return null;
            }

            Resource resource = fileDbService.downloadFile(userId, chatId, fileId);

            if (resource == null) {
                logger.warn("Failed to download file");
                return null;
            }

            logger.info("Successfully downloading file to client");
            return resource;

        } catch (Exception e) {
            logger.error("Failed to download file: {}", e.getMessage());
            return null;
        }
    }

    public boolean deleteFile(String userId, String chatId, String fileId) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(chatId) || !validateId(fileId)) {
                logger.warn("Failed to delete file: user, chat, or file ID is null or empty");
                return false;
            }

            boolean deleted = fileDbService.deleteFile(userId, chatId, fileId);

            if (!deleted) {
                logger.warn("Failed to delete file");
                return false;
            }

            logger.info("Successfully deleted file");
            return true;

        } catch (Exception e) {
            logger.error("Failed to delete file: {}", e.getMessage());
            return false;
        }
    }

    public List<FileDTO> getChatFiles(String userId, String chatId) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(chatId)) {
                logger.warn("Failed to retrieve files list: user or chat ID is null or empty");
                return List.of();
            }

            List<File> chatFiles = fileDbService.getChatFiles(userId, chatId);

            if (chatFiles == null) {
                logger.warn("Failed to retrieve list of files from chat {}", chatId);
                return List.of();
            }

            // Convert to DTO and return
            logger.info("Successfully retrieved list of files from chat {}", chatId);
            return fileMapper.toDTOList(chatFiles);

        } catch (Exception e) {
            logger.error("Failed to retrieve files list: {}", e.getMessage());
            return List.of();
        }
    }

    public String getFileIdFromFilename(String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                logger.warn("Failed to retrieve file ID: file name is null or empty");
                return null;
            }
            return fileDbService.getFileIdByFilename(filename);
        } catch (Exception e) {
            logger.error("Failed to retrieve file ID from file name: {}", e.getMessage());
            return null;
        }
    }

    private boolean validateId(String Id) {
        return Id != null && !Id.trim().isEmpty();
    }

}
