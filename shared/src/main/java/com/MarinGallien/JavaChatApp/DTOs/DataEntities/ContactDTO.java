package com.MarinGallien.JavaChatApp.DTOs.DataEntities;

import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;

public class ContactDTO {
    private String userId;
    private String username;
    private OnlineStatus onlineStatus;

    public ContactDTO(String userId, String username, OnlineStatus onlineStatus) {
        this.userId = userId;
        this.username = username;
        this.onlineStatus = onlineStatus;
    }

    public String getUserId() {return userId;}
    public String getUsername() {return username;}
    public OnlineStatus getOnlineStatus() {return onlineStatus;}

    public void setUserId(String userId) {this.userId = userId;}
    public void setUsername(String username) {this.username = username;}
    public void setOnlineStatus(OnlineStatus onlineStatus) {this.onlineStatus = onlineStatus;}
}
