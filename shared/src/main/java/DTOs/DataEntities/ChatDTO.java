package DTOs.DataEntities;

import Enums.ChatType;

import java.time.LocalDateTime;
import java.util.List;

public class ChatDTO {
    private String chatId;
    private ChatType chatType;
    private String chatName;
    private String creatorId;
    private LocalDateTime createdAt;
    private List<String> participantIds;

    public ChatDTO(String chatId, ChatType chatType, String chatName, String creatorId, LocalDateTime createdAt,
                   List<String> participantIds) {
        this.chatId = chatId;
        this.chatType = chatType;
        this.chatName = chatName;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.participantIds = participantIds;
    }

    public String getChatId() {return chatId;}
    public ChatType getChatType() {return chatType;}
    public String getChatName() {return chatName;}
    public String getCreatorId() {return creatorId;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public List<String> getParticipantIds() {return participantIds;}

    public void setChatId(String chatId) {this.chatId = chatId;}
    public void setChatType(ChatType chatType) {this.chatType = chatType;}
    public void setChatName(String chatName) {this.chatName = chatName;}
    public void setCreatorId(String creatorId) {this.creatorId = creatorId;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setParticipantIds(List<String> participantIds) {this.participantIds = participantIds;}
}
