package com.MarinGallien.JavaChatApp.Database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    public File() {};

    public File(String fileId, String filename, Long fileSize, String fileType, String chatId) {
        this.fileId = fileId;
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.chatId = chatId;
    }

    public String getFileId() {return fileId;}
    public String getFilename() {return filename;}
    public Long getFileSize() {return fileSize;}
    public String getFileType() {return fileType;}
    public String getChatId() {return chatId;}

    public void setFileId(String fileId) {this.fileId = fileId;}
    public void setFilename(String filename) {this.filename = filename;}
    public void setFileSize(Long fileSize) {this.fileSize = fileSize;}
    public void setFileType(String fileType) {this.fileType = fileType;}
    public void setChatId(String chatId) {this.chatId = chatId;}
}
