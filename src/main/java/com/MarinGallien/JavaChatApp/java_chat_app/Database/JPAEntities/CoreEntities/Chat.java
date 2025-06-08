package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.ChatParticipant;

import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Chat {
    // Columns:

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chat_id")
    private String chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type", nullable = false)
    private ChatType chatType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Size(max = 100, message = "Chat name cannot exceed 100 characters")
    @Column(name = "chat_name")
    private String chatName;


    // Relationships:

    // Relationship of chat to messages
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    // Relationship of chat to participants
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ChatParticipant> participants = new HashSet<>();


    // Constructors
    public Chat() {}

    public Chat(ChatType chatType) {
        this.chatType = chatType;
    }

    public Chat(ChatType chatType, String chatName) {
        this.chatType = chatType;
        this.chatName = chatName;
    }


    // Getters
    public String getChatId() {return chatId;}
    public ChatType getChatType() {return chatType;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public String getChatName() {return chatName;}
    public Set<Message> getMessages() {return messages;}
    public Set<ChatParticipant> getParticipants() {return participants;}


    // Setters
    public void setChatId(String chatId) {this.chatId = chatId;}
    public void setChatType(ChatType chatType) {this.chatType = chatType;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setChatName(String chatName) {this.chatName = chatName;}
    public void setMessages(Set<Message> messages) {this.messages = messages;}
    public void setParticipants(Set<ChatParticipant> participants) {this.participants = participants;}


    // Helper methods

    public void addParticipant(ChatParticipant participant) {
        participants.add(participant);
        participant.setChat(this);
    }

    public void removeParticipant(ChatParticipant participant) {
        participants.remove(participant);
        participant.setChat(null);
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setChat(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Chat chat = (Chat) obj;
        return this.chatId != null ? this.chatId.equals(chat.getChatId()) : chat.getChatId() == null;
    }

    @Override
    public int hashCode() {
        return chatId != null ? chatId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatId='" + chatId + '\'' +
                ", chatType=" + chatType +
                ", chatName='" + chatName + '\'' +
                ", createdAt=" + createdAt +
                ", participantCount=" + participants.size() +
                '}';
    }
}
