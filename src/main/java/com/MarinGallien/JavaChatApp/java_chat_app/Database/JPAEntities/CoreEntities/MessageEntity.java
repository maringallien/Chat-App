package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.MessageFile;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.MessageRecipient;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.MessageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.MarinGallien.JavaChatApp.java_chat_app.Enums.MessageType;

@Entity
@Table(name = "messages")
public class MessageEntity {

    // Columns

    // Create primary key column
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id")
    private String messageId;

    // Create sender column
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    // Create chat column
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

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

    // Message to recipients relationship
    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MessageRecipient> recipients = new HashSet<>();

    // Message to files relationship
    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MessageFile> messageFiles = new HashSet<>();


    // Constructors

    public MessageEntity() {}

    public MessageEntity(UserEntity sender, ChatEntity chat, String content, MessageType messageType) {
        this.sender = sender;
        this.chat = chat;
        this.content = content;
        this.messageType = messageType;
    }


    // Getters
    public String getMessageId() {
        return messageId;
    }
    public UserEntity getSender() {
        return sender;
    }
    public ChatEntity getChat() {
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
    public Set<MessageRecipient> getRecipients() {
        return recipients;
    }
    public Set<MessageFile> getMessageFiles() {
        return messageFiles;
    }


    // Setters
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    public void setSender(UserEntity sender) {
        this.sender = sender;
    }
    public void setChat(ChatEntity chat) {
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
    public void setRecipients(Set<MessageRecipient> recipients) {
        this.recipients = recipients;
    }
    public void setMessageFiles(Set<MessageFile> messageFiles) {
        this.messageFiles = messageFiles;
    }


    // Helper Methods

    // Adds a recipient to the message
    public void addRecipient(MessageRecipient recipient) {
        recipients.add(recipient);
    }

    // Associates a file with a message
    public void addFile(MessageFile messageFile) {
        messageFiles.add(messageFile);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MessageEntity message = (MessageEntity) obj;
        return messageId != null ? messageId.equals(message.messageId) : message.messageId == null;
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
