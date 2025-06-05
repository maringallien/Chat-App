package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.BaseMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.MessageType;
import jakarta.validation.constraints.NotBlank;

public class WebSocketMessage extends BaseMessage {
    // Parameters
    @NotBlank(message = "Room ID cannot be blank")
    private String roomID;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotBlank(message = "Recipient ID cannot be blank")
    private String recipientID;

    // Default constructor for Jackson
    public WebSocketMessage() {
        super();
    }

    // Constructor for creating text messages in code
    public WebSocketMessage(String senderID, String roomID, String content, String recipientID) {
        super(MessageType.TEXT_MESSAGE, senderID);
        this.roomID = roomID;
        this.content = content;
        this.recipientID = recipientID;

    }


}
