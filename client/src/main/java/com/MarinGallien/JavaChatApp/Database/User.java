package com.MarinGallien.JavaChatApp.Database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "username", nullable = false)
    private String username;

    public User() {}

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {return userId;}
    public String getUsername() {return username;}

    public void setUserId(String userId) {this.userId = userId;}
    public void setUsername(String username) {this.username = username;}
}
