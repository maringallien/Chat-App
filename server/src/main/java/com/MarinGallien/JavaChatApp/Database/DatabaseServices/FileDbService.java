package com.MarinGallien.JavaChatApp.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.JPAEntities.Chat;
import com.MarinGallien.JavaChatApp.JPAEntities.File;
import com.MarinGallien.JavaChatApp.JPAEntities.Message;
import com.MarinGallien.JavaChatApp.JPAEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.*;
import com.MarinGallien.JavaChatApp.Enums.MessageType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FileDbService {

    private Logger logger = LoggerFactory.getLogger(FileDbService.class);

    private final FileRepo fileRepo;
    private final UserRepo userRepo;
    private final ChatRepo chatRepo;
    private final ChatParticipantRepo chatParticipantRepo;
    private final MessageRepo messageRepo;

    public FileDbService(FileRepo fileRepo, UserRepo userRepo, ChatRepo chatRepo,
                         ChatParticipantRepo chatParticipantRepo, MessageRepo messageRepo) {
        this.fileRepo = fileRepo;
        this.userRepo = userRepo;
        this.chatRepo = chatRepo;
        this.chatParticipantRepo = chatParticipantRepo;
        this.messageRepo = messageRepo;
    }

    public File uploadFile(String userId, String chatId, MultipartFile file) {
        try {
            // Input validation
            if (!userRepo.existsById(userId) || !chatRepo.existsById(chatId)) {
                logger.warn("Failed to upload file: user {} or chat {} does not exist", userId, chatId);
                return null;
            }

            if (!chatParticipantRepo.existsByChatChatIdAndUserUserId(chatId, userId)) {
                logger.warn("Failed to upload file: user {} is not a member of chat {}", userId, chatId);
                return null;
            }

            if (file == null || file.isEmpty()) {
                logger.warn("Failed to upload file: file is null or empty");
                return null;
            }

            // Save file to disk first
            String filePath = saveFileToDisk(file);
            if (filePath == null) {
                logger.warn("Failed to save file to disk");
                return null;
            }

            // Get entities from database
            User uploader = userRepo.findUserById(userId);
            Chat chat = chatRepo.findChatById(chatId);

            // Create and save MESSAGE first (required for file foreign key)
            Message fileMessage = new Message(
                    uploader,
                    chat,
                    "File: " + file.getOriginalFilename(),
                    MessageType.TEXT_MESSAGE
            );
            Message savedMessage = messageRepo.save(fileMessage);

            // Create file entity WITH message reference
            File newFile = new File(
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getContentType(),
                    filePath,
                    uploader
            );

            // Set the message before saving
            newFile.setMessage(savedMessage);

            // Save file to database
            File savedFile = fileRepo.save(newFile);

            // Add file to message's file collection (bidirectional relationship)
            savedMessage.addFile(savedFile);
            messageRepo.save(savedMessage);

            logger.info("Successfully saved file {} to chat {}", file.getOriginalFilename(), chatId);
            return savedFile;

        } catch (Exception e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            return null;
        }
    }

    private String saveFileToDisk(MultipartFile file) {
        try {
            // Generate unique fileName
            String originalFileName = file.getOriginalFilename();
            String extension = getFileExtension(originalFileName);
            String uniqueFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            // Create upload directory if it doesn't already exist
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save file
            Path filePath = uploadPath.resolve(uniqueFileName);
            file.transferTo(filePath.toFile());

            return filePath.toString();
        } catch (Exception e) {
            logger.error("Failed to save file to disk: {}", e.getMessage());
            return null;
        }
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    public Resource downloadFile(String userId, String chatId, String fileId) {
        try {
            // Input validation
            if (!userRepo.existsById(userId) || !chatRepo.existsById(chatId) || !fileRepo.existsById(fileId)) {
                logger.warn("Failed to download file: user {} or file {} does not exist", userId, fileId);
                return null;
            }

            if (!chatParticipantRepo.existsByChatChatIdAndUserUserId(chatId, userId)) {
                logger.warn("Failed to download file: user {} is not a member of chat {}", userId, chatId);
                return null;
            }

            // Retrieve file metadata from database
            File file = fileRepo.findById(fileId).orElse(null);
            if (file == null) {
                logger.warn("File {} not found in database", fileId);
                return null;
            }

            // Check that the file belongs to the chat
            String fileChatId = file.getMessage().getChat().getChatId();
            if (!fileChatId.equals(chatId)){
                logger.warn("Failed to download file: file {} does not belong to chat {}", fileId, chatId);
                return null;
            }

            // Check that file exists on disk
            Path filepath = Paths.get(file.getFilePath());
            if (!Files.exists(filepath)) {
                logger.warn("Failed to download file: file {} not found on disk", fileId);
                return null;
            }

            // Create a streaming resource
            InputStream inputStream = Files.newInputStream(filepath);
            Resource resource = new InputStreamResource(inputStream);

            logger.info("Successfully prepared file {} for download by user {}", fileId, userId);

            return resource;

        } catch (Exception e) {
            logger.error("Failed to download file: {}", e.getMessage());
            return null;
        }
    }

    public Boolean deleteFile(String userId, String chatId, String fileId) {
        try {
            // Input validation
            if (!userRepo.existsById(userId) || !chatRepo.existsById(chatId) || !fileRepo.existsById(fileId)) {
                logger.warn("Failed to delete file: user {}, chat {}, or file {} does not exist", userId, chatId, fileId);
                return false;
            }

            if (!chatParticipantRepo.existsByChatChatIdAndUserUserId(chatId, userId)) {
                logger.warn("Failed to delete file: user {} is not a member of chat {}", userId, chatId);
                return false;
            }

            // Retrieve file metadata
            File file = fileRepo.findById(fileId).orElse(null);

            // Verify it is not null
            if (file == null) {
                logger.warn("Failed to delete file: file {} does not exist", fileId);
                return false;
            }

            // Make sure the file belongs to the chat
            String fileChatId = file.getMessage().getChat().getChatId();
            if (!fileChatId.equals(chatId)) {
                logger.warn("Failed to delete file: file {} does not belong to chat {}", fileId, chatId);
                return false;
            }

            // Make sure user can delete it - only creator can delete
            if (!file.getUploader().getUserId().equals(userId)){
                logger.warn("Failed to delete file: user {} did not create the file", userId);
                return false;
            }

            // Get the message associated with this file
            Message associatedMessage = file.getMessage();

            // Delete the MESSAGE (this will cascade delete the file)
            messageRepo.delete(associatedMessage);

            // Verify deletion succeeded
            if (fileRepo.existsById(fileId)) {
                logger.error("Failed to delete file {} from database", fileId);
                return false;
            }

            // Delete file from file system - don't return false if that fails because file was still deleted from db
            Path filepath = Paths.get(file.getFilePath());
            if (!Files.exists(filepath)) {
               logger.warn("Failed to delete file from filesystem: {}", filepath);
            }

            Files.delete(filepath);
            logger.info("Deleted file from filesystem: {}", filepath);
            return true;

        } catch (Exception e) {
            logger.error("Failed to delete file: {}", e.getMessage());
            return false;
        }
    }

    public List<File> getChatFiles(String userId, String chatId) {
        try {
            // Input validation
            if (!userRepo.existsById(userId) || !chatRepo.existsById(chatId)) {
                logger.warn("Failed to retrieve files list: user {} or file {} does not exist", userId, chatId);
                return List.of();
            }

            if (!chatParticipantRepo.existsByChatChatIdAndUserUserId(chatId, userId)) {
                logger.warn("Failed to retrieve files list: user {} is not a member of chat {}", userId, chatId);
                return List.of();
            }

            // Get all files for this chat
            List<File> chatFiles = fileRepo.findByMessageChatChatId(chatId);

            if (chatFiles == null) {
                logger.warn("Failed to retrieve files for chat {}", chatId);
                return List.of();
            }

            logger.info("Successfully retrieved {} files for chat {}", chatFiles.size(), chatId);
            return chatFiles;

        } catch (Exception e) {
            logger.error("Failed to retrieve files list: {}", e.getMessage());
            return List.of();
        }
    }
}
