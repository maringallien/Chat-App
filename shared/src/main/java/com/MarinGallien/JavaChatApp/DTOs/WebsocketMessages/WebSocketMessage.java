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

    // Default constructor for Jackson
    public WebSocketMessage() {
        super();
    }

    public WebSocketMessage(String senderId, String chatID, String content, String username) {
        super(MessageType.TEXT_MESSAGE, senderId, username);
        this.chatID = chatID;
        this.content = content;
    }

    // Getters
    public String getContent() { return content; }
    public String getChatID() { return chatID; }

    // Setters
    public void setContent(String content) {this.content = content;}
    public void setChatID(String chatID) { this.chatID = chatID; }
}

