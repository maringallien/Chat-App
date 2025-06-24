package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.File;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService {

    private static Logger logger = LoggerFactory.getLogger(FileService.class);

    public File uploadFile(String userId, String chatId, MultipartFile file) {
        try {
            // Validate inputs
            if (!validateId(userId)) {
                logger.warn("Failed to upload file: user ID is null or empty");
                return null;
            }

            if (file == null || file.isEmpty()) {
                logger.warn("Failed to upload file: file is null or empty");
                return null;
            }

        } catch (Exception e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            return null;
        }
    }

    public Resource downloadFile(String userId, String chatId, String fileId) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(fileId)) {
                logger.warn("Failed to download file: user or file ID is null or empty");
                return null;
            }


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


        } catch (Exception e) {
            logger.error("Failed to delete file: {}", e.getMessage());
            return false;
        }
    }

    public List<File> getChatFiles(String userId, String chatId) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(chatId)) {
                logger.warn("Failed to retrieve files list: user or file ID is null or empty");
                return null;
            }



        } catch (Exception e) {
            logger.error("Failed to retrieve files list: {}", e.getMessage());
            return null;
        }
    }

    private boolean validateId(String Id) {
        return Id != null && !Id.trim().isEmpty();
    }

}
