package com.MarinGallien.JavaChatApp.DTOs.DataEntities;

import java.time.LocalDateTime;

public class FileDTO {
    private String fileId;
    private String filename;
    private Long fileSize;
    private String fileType;
    private LocalDateTime sentAt;
    private String uploaderId;
    private String uploaderUsername;
    private String chatId;

    // Default constructor
    public FileDTO() {}

    // Constructor with all fields
    public FileDTO(String fileId, String filename, Long fileSize, String fileType,
                   LocalDateTime sentAt, String uploaderId, String uploaderUsername, String chatId) {
        this.fileId = fileId;
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.sentAt = sentAt;
        this.uploaderId = uploaderId;
        this.uploaderUsername = uploaderUsername;
        this.chatId = chatId;
    }

    // Getters
    public String getFileId() { return fileId; }
    public String getFilename() { return filename; }
    public Long getFileSize() { return fileSize; }
    public String getFileType() { return fileType; }
    public LocalDateTime getSentAt() { return sentAt; }
    public String getUploaderId() { return uploaderId; }
    public String getUploaderUsername() { return uploaderUsername; }
    public String getChatId() { return chatId; }

    // Setters
    public void setFileId(String fileId) { this.fileId = fileId; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }
    public void setUploaderUsername(String uploaderUsername) { this.uploaderUsername = uploaderUsername; }
    public void setChatId(String chatId) { this.chatId = chatId; }
}