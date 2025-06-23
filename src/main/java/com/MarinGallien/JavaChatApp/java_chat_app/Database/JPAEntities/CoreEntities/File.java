package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
public class File {
    // Columns
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "file_id")
    private String fileId;

    @Column(name = "file_name", nullable = false)
    private String filename;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    // Create many-to-one relationship between file and uploader, and create foreign key column
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;


    // Constructors
    public File() {}

    public File(String filename, Long fileSize, String fileType, String filePath, User uploader) {
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.filePath = filePath;
        this.uploader = uploader;

    }


    // Getters
    public String getFileId() {return fileId;}
    public String getFilename() {return filename;}
    public Long getFileSize() {return fileSize;}
    public String getFileType() {return fileType;}
    public LocalDateTime getSentAt() {return sentAt;}
    public String getFilePath() {return filePath;}
    public User getUploader() {return uploader;}
    public Message getMessage() {return message;}


    // Setters
    public void setFileId(String fileId) {this.fileId = fileId;}
    public void setFilename(String filename) {this.filename = filename;}
    public void setFileSize(Long fileSize) {this.fileSize = fileSize;}
    public void setFileType(String fileType) {this.fileType = fileType;}
    public void setSentAt(LocalDateTime sentAt) {this.sentAt = sentAt;}
    public void setFilePath(String filePath) {this.filePath = filePath;}
    public void setUploader(User uploader) {this.uploader = uploader;}
    public void setMessage(Message message) {this.message = message;}


    // Helper methods

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        File file = (File) obj;
        return fileId != null ? fileId.equals(file.fileId) : file.fileId == null;
    }

    @Override
    public int hashCode() {
        return fileId != null ? fileId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "fileId='" + fileId + '\'' +
                ", filename='" + filename + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", sentAt=" + sentAt +
                ", filePath='" + filePath + '\'' +
                ", uploaderId='" + (uploader != null ? uploader.getUserId() : null) + '\'' +
                ", messageId='" + (message != null ? message.getMessageId() : null) + '\'' +
                '}';
    }
}
