package com.MarinGallien.JavaChatApp.DTOs.DataEntities;

import com.MarinGallien.JavaChatApp.Enums.ChatType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ChatDTO {
    private String chatId;
    private ChatType chatType;
    private String chatName;
    private String creatorId;
    private List<String> participantIds;
    private LocalDateTime createdAt;

    public ChatDTO() {
    }

    public ChatDTO(String chatId, ChatType chatType, String chatName, String creatorId,
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
