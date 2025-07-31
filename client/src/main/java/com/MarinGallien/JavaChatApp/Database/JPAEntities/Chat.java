package com.MarinGallien.JavaChatApp.Database.JPAEntities;

import com.MarinGallien.JavaChatApp.Enums.ChatType;
import com.sun.jdi.CharType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(name = "chat_type", nullable = false)
    private ChatType chatType;

    @Column(name = "chat_name", nullable = false)
    private String chatName;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "participant_ids", nullable = false)
    private List<String> participantIds;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    public Chat(String chatId, ChatType chatType, String chatName, String creatorId,
                List<String> participantIds, LocalDateTime createdAt) {
        this.chatId = chatId;
        this.chatType = chatType;
        this.chatName = chatName;
        this.creatorId = creatorId;
        this.participantIds = participantIds;
        this.createdAt = createdAt;
    }

    public String getChatId() {return chatId;}
    public ChatType getChatType() {return chatType;}
    public String getChatName() {return chatName;}
    public String getCreatorId() {return creatorId;}
    public List<String> getParticipantIds() {return participantIds;}
    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setChatId(String chatId) {this.chatId = chatId;}
    public void setChatType(ChatType chatType) {this.chatType = chatType;}
    public void setChatName(String chatName) {this.chatName = chatName;}
    public void setCreatorId(String creatorId) {this.creatorId = creatorId;}
    public void setParticipantIds(List<String> participantIds) {this.participantIds = participantIds;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}
