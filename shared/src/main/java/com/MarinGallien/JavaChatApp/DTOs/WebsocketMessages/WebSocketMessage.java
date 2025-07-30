package com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages;

import com.MarinGallien.JavaChatApp.Enums.MessageType;
import jakarta.validation.constraints.NotBlank;

public class WebSocketMessage extends BaseMessage {
    // Parameters
    @NotBlank(message = "chat ID cannot be blank")
    private String chatID;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    // Default constructor for Jackson
    public WebSocketMessage() {
        super();
    }

    public WebSocketMessage(String chatID, String content) {
        this.chatID = chatID;
        this.content = content;
    }

    // Getters
    public String getContent() { return content; }
    public String getChatID() { return chatID; }

    // Setters
    public void setContent(String content) {this.content = content;}
    public void setChatID(String chatID) { this.chatID = chatID; }

    // Constructor for creating text messages in code
    public WebSocketMessage(String senderID, String chatID, String content) {
        super(MessageType.TEXT_MESSAGE, senderID);
        this.chatID = chatID;
        this.content = content;

    }


}

