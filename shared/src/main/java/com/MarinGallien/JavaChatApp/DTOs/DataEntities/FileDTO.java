package com.MarinGallien.JavaChatApp.DTOs.DataEntities;

import java.time.LocalDateTime;

public class FileDTO {
    private String fileId;
    private String filename;
    private Long fileSize;
    private String fileType;
    private String chatId;
    private LocalDateTime sentAt;


    // Default constructor
    public FileDTO() {}

    // Constructor with all fields
    public FileDTO(String fileId, String filename, Long fileSize, String fileType, String chatId, LocalDateTime sentAt) {
        this.fileId = fileId;
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.chatId = chatId;
        this.sentAt = sentAt;
    }

    // Getters
    public String getFileId() { return fileId; }
    public String getFilename() { return filename; }
    public Long getFileSize() { return fileSize; }
    public String getFileType() { return fileType; }
    public String getChatId() { return chatId; }
    public LocalDateTime getSentAt() {return sentAt;}

    // Setters
    public void setFileId(String fileId) { this.fileId = fileId; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public void setSentAt(LocalDateTime sentAt) {this.sentAt = sentAt;}
}