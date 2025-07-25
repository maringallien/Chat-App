package com.MarinGallien.JavaChatApp.DTOs.DataEntities;

import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;

import java.time.LocalDateTime;

public class UserDTO {
    private String userId;
    private String username;

    public UserDTO(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {return userId;}
    public String getUsername() {return username;}

    public void setUserId(String userId) {this.userId = userId;}
    public void setUsername(String username) {this.username = username;}
}
