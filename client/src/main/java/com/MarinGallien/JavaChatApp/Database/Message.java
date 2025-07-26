package com.MarinGallien.JavaChatApp.Database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(name = "content", nullable = false)
    private String content;

    public Message() {};

    public Message(String messageId, String senderId, String chatId, String content) {
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
