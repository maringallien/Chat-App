package com.MarinGallien.JavaChatApp.DTOs.DataEntities;

import com.MarinGallien.JavaChatApp.Enums.MessageType;

import java.time.LocalDateTime;

public class MessageDTO {
    private String messageId;
    private String senderId;
    private String chatId;
    private String content;

    public MessageDTO(String messageId, String senderId, String chatId, String content) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.chatId = chatId;
        this.content = content;
    }

    public String getMessageId() {return messageId;}
    public String getSenderId() {return senderId;}
    public String getChatId() {return chatId;}
    public String getContent() {return content;}

    public void setMessageId(String messageId) {this.messageId = messageId;}
    public void setSenderId(String senderId) {this.senderId = senderId;}
    public void setChatId(String chatId) {this.chatId = chatId;}
    public void setContent(String content) {this.content = content;}
}
