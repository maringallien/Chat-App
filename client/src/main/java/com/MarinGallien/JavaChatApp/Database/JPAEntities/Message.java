package com.MarinGallien.JavaChatApp.Database.JPAEntities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

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

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    public Message() {};

    public Message(String messageId, String senderId, String chatId, String content, LocalDateTime sentAt) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.chatId = chatId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public String getMessageId() {return messageId;}
    public String getSenderId() {return senderId;}
    public String getChatId() {return chatId;}
    public String getContent() {return content;}
    public LocalDateTime getSentAt() {return sentAt;}

    public void setMessageId(String messageId) {this.messageId = messageId;}
    public void setSenderId(String senderId) {this.senderId = senderId;}
    public void setChatId(String chatId) {this.chatId = chatId;}
    public void setContent(String content) {this.content = content;}
    public void setSentAt(LocalDateTime sentAt) {this.sentAt = sentAt;}
}
