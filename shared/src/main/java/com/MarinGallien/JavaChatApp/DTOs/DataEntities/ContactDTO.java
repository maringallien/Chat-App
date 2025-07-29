package com.MarinGallien.JavaChatApp.DTOs.DataEntities;

import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;

import java.time.LocalDateTime;

public class ContactDTO {
    private String userId;
    private String username;
    private OnlineStatus onlineStatus;
    private LocalDateTime createdAt;

    public ContactDTO(String userId, String username, OnlineStatus onlineStatus, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.onlineStatus = onlineStatus;
        this.createdAt = createdAt;
    }

    public String getUserId() {return userId;}
    public String getUsername() {return username;}
    public OnlineStatus getOnlineStatus() {return onlineStatus;}
    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setUserId(String userId) {this.userId = userId;}
    public void setUsername(String username) {this.username = username;}
    public void setOnlineStatus(OnlineStatus onlineStatus) {this.onlineStatus = onlineStatus;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}
