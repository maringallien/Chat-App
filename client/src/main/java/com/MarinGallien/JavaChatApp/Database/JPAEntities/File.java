package com.MarinGallien.JavaChatApp.Database.JPAEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
public class File {

    @Id
    @Column(name = "file_id", nullable = false)
    private String fileId;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    public File() {};

    public File(String fileId, String filename, Long fileSize, String fileType, String chatId, LocalDateTime sentAt) {
        this.fileId = fileId;
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.chatId = chatId;
        this.sentAt = sentAt;
    }

    public String getFileId() {return fileId;}
    public String getFilename() {return filename;}
    public Long getFileSize() {return fileSize;}
    public String getFileType() {return fileType;}
    public String getChatId() {return chatId;}
    public LocalDateTime getSentAt() {return sentAt;}

    public void setFileId(String fileId) {this.fileId = fileId;}
    public void setFilename(String filename) {this.filename = filename;}
    public void setFileSize(Long fileSize) {this.fileSize = fileSize;}
    public void setFileType(String fileType) {this.fileType = fileType;}
    public void setChatId(String chatId) {this.chatId = chatId;}
    public void setSentAt(LocalDateTime sentAt) {this.sentAt = sentAt;}
}
