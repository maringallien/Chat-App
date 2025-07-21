package com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages;

import com.MarinGallien.JavaChatApp.Enums.MessageType;
import jakarta.validation.constraints.NotBlank;

public class WebSocketMessage extends BaseMessage {
    // Parameters
    @NotBlank(message = "chat ID cannot be blank")
    private String chatID;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotBlank(message = "Recipient ID cannot be blank")
    private String recipientID;

    // Default constructor for Jackson
    public WebSocketMessage() {
        super();
    }

    // Getters
    public String getContent() { return content; }
    public String getChatID() { return chatID; }
    public String getRecipientID() {return recipientID; }

    // Setters
    public void setContent(String content) {this.content = content;}
    public void setChatID(String chatID) { this.chatID = chatID; }
    public void setRecipientID(String recipientID) { this.recipientID = recipientID; }

    // Constructor for creating text messages in code
    public WebSocketMessage(String senderID, String chatID, String content, String recipientID) {
        super(MessageType.TEXT_MESSAGE, senderID);
        this.chatID = chatID;
        this.content = content;
        this.recipientID = recipientID;

    }


}

