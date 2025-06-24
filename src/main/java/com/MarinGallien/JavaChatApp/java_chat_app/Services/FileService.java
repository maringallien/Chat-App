package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.FileDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.File;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Requests.DeleteChatRequest;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.FileEvents.DeleteFileRequest;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService {

    private static Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    FileDbService fileDbService;

    public boolean uploadFile(String userId, String chatId, MultipartFile file) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(chatId)) {
                logger.warn("Failed to upload file: user or chat ID is null or empty");
                return false;
            }

            if (file == null || file.isEmpty()) {
                logger.warn("Failed to upload file: file is null or empty");
                return false;
            }

            File uploadedFile = fileDbService.uploadFile(userId, chatId, file);

            if (uploadedFile == null) {
                logger.warn("Failed to upload file");
                return false;
            }

            logger.info("Successfully uploaded file");
            return true;

        } catch (Exception e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            return false;
        }
    }

    public Resource downloadFile(String userId, String chatId, String fileId) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(fileId)) {
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

    @EventListener
    @Async("eventTaskExecutor")
    public void deleteFile(DeleteFileRequest event) {
        try {
            // Validate inputs
            if (!validateId(event.userId()) || !validateId(event.chatId()) || !validateId(event.fileId())) {
                logger.warn("Failed to delete file: user, chat, or file ID is null or empty");
                return;
            }

            boolean deleted = fileDbService.deleteFile(event.userId(), event.chatId(), event.fileId());

            if (!deleted) {
                logger.warn("Failed to delete file");
                return;
            }

            logger.info("Successfully deleted file");
            return;

        } catch (Exception e) {
            logger.error("Failed to delete file: {}", e.getMessage());
            return;
        }
    }

    public List<File> getChatFiles(String userId, String chatId) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(chatId)) {
                logger.warn("Failed to retrieve files list: user or chat ID is null or empty");
                return null;
            }

            List<File> chatFiles = fileDbService.getChatFiles(userId, chatId);

            if (chatFiles == null) {
                logger.warn("Failed to retrieve list of files from chat {}", chatId);
                return null;
            }

            logger.info("Successfully retrieved list of files from chat {}", chatId);
            return chatFiles;

        } catch (Exception e) {
            logger.error("Failed to retrieve files list: {}", e.getMessage());
            return null;
        }
    }

    private boolean validateId(String Id) {
        return Id != null && !Id.trim().isEmpty();
    }

}
