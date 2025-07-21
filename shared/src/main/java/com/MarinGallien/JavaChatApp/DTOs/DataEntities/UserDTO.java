package com.MarinGallien.JavaChatApp.DTOs.DataEntities;

import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;

import java.time.LocalDateTime;

public class UserDTO {
    private String userId;
    private String username;
    private String email;
    private OnlineStatus status;
    private LocalDateTime dateJoined;

    public UserDTO(String userId, String username, String email, OnlineStatus status, LocalDateTime dateJoined) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.status = status;
        this.dateJoined = dateJoined;
    }

    public String getUserId() {return userId;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public OnlineStatus getStatus() {return status;}
    public LocalDateTime getDateJoined() {return dateJoined;}

    public void setUserId(String userId) {this.userId = userId;}
    public void setUsername(String username) {this.username = username;}
    public void setEmail(String email) {this.email = email;}
    public void setStatus(OnlineStatus status) {this.status = status;}
    public void setDateJoined(LocalDateTime dateJoined) {this.dateJoined = dateJoined;}
}
