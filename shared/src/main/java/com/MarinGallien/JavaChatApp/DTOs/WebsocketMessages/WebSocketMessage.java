package com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages;

import com.MarinGallien.JavaChatApp.Enums.MessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class WebSocketMessage extends BaseMessage {
    // Parameters
    @NotBlank(message = "chat ID cannot be blank")
    @JsonProperty("chat_id")
    private String chatID;

    @NotBlank(message = "Content cannot be blank")
    @JsonProperty("content")
    private String content;

    @NotBlank(message = "Sender name cannot be blank")
    @JsonProperty("username")
    private String username;

    // Default constructor for Jackson
    public WebSocketMessage() {
        super();
    }

    public WebSocketMessage(String senderId, String chatID, String content, String username) {
        super(MessageType.TEXT_MESSAGE, senderId);
        this.chatID = chatID;
        this.content = content;
        this.username = username;
    }

    // Getters
    public String getContent() { return content; }
    public String getChatID() { return chatID; }
    public String getUsername() {return username;}

    // Setters
    public void setContent(String content) {this.content = content;}
    public void setChatID(String chatID) { this.chatID = chatID; }
    public void setUsername(String username) {this.username = username;}
}

