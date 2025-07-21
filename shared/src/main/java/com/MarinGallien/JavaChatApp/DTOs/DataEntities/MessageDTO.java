package com.MarinGallien.JavaChatApp.DTOs.DataEntities;

import com.MarinGallien.JavaChatApp.Enums.MessageType;

import java.time.LocalDateTime;

public class MessageDTO {
    private String messageId;
    private String senderId;
    private String senderUsername;
    private String chatId;
    private String content;
    private LocalDateTime sentAt;
    private MessageType messageType;

    public MessageDTO(String messageId, String senderId, String senderUsername, String chatId, String content,
                      LocalDateTime sentAt, MessageType messageType) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.chatId = chatId;
        this.content = content;
        this.sentAt = sentAt;
        this.messageType = messageType;
    }

    public String getMessageId() {return messageId;}
    public String getSenderId() {return senderId;}
    public String getSenderUsername() {return senderUsername;}
    public String getChatId() {return chatId;}
    public String getContent() {return content;}
    public LocalDateTime getSentAt() {return sentAt;}
    public MessageType getMessageType() {return messageType;}

    public void setMessageId(String messageId) {this.messageId = messageId;}
    public void setSenderId(String senderId) {this.senderId = senderId;}
    public void setSenderUsername(String senderUsername) {this.senderUsername = senderUsername;}
    public void setChatId(String chatId) {this.chatId = chatId;}
    public void setContent(String content) {this.content = content;}
    public void setSentAt(LocalDateTime sentAt) {this.sentAt = sentAt;}
    public void setMessageType(MessageType messageType) {this.messageType = messageType;}
}
