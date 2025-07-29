package com.MarinGallien.JavaChatApp.Database.JPAEntities;

import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class Contact {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OnlineStatus status = OnlineStatus.OFFLINE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Contact() {}

    public Contact(String userId, String username, OnlineStatus status, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getUserId() {return userId;}
    public String getUsername() {return username;}
    public OnlineStatus getStatus() {return status;}
    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setUserId(String userId) {this.userId = userId;}
    public void setUsername(String username) {this.username = username;}
    public void setStatus(OnlineStatus status) {this.status = status;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}
