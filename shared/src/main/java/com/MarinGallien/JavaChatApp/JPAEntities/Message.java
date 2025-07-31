package com.MarinGallien.JavaChatApp.JPAEntities;

import com.MarinGallien.JavaChatApp.Enums.MessageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "messages")
public class Message {

    // Columns

    // Create primary key column
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id")
    private String messageId;

    // Create sender column
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // Create chat column
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    // Create message content column
    @NotBlank
    @Size(max = 5000, message = "Message content cannot exceed 5000 characters")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // Create sent_at column
    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    // Create message type column
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT_MESSAGE;


    // Relationships

    // Message to files relationship
    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> messageFileEntities = new HashSet<>();


    // Constructors

    public Message() {}

    public Message(User sender, Chat chat, String content, MessageType messageType) {
        this.sender = sender;
        this.chat = chat;
        this.content = content;
        this.messageType = messageType;
    }


    // Getters
    public String getMessageId() {
        return messageId;
    }
    public User getSender() {
        return sender;
    }
    public Chat getChat() {
        return chat;
    }
    public String getContent() {
        return content;
    }
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    public MessageType getMessageType() {
        return messageType;
    }
    public Set<File> getMessageFiles() {
        return messageFileEntities;
    }


    // Setters
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    public void setSender(User sender) {
        this.sender = sender;
    }
    public void setChat(Chat chat) {
        this.chat = chat;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
    public void setMessageFiles(Set<File> messageFileEntities) {this.messageFileEntities = messageFileEntities;}


    // Helper Methods

    // Associates a file with a message
    public void addFile(File messageFile) {messageFileEntities.add(messageFile);}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message message = (Message) obj;
        return messageId != null ? messageId.equals(message.messageId) : message.messageId == null;
    }

    @Override
    public int hashCode() {
        return messageId != null ? messageId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", senderId='" + (sender != null ? sender.getUserId() : null) + '\'' +
                ", chatId='" + (chat != null ? chat.getChatId() : null) + '\'' +
                ", messageType=" + messageType +
                ", sentAt=" + sentAt +
                '}';
    }
}
