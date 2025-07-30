package com.MarinGallien.JavaChatApp.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Chat;
import com.MarinGallien.JavaChatApp.Database.JPARepos.ChatRepo;
import com.MarinGallien.JavaChatApp.Enums.ChatType;
import com.MarinGallien.JavaChatApp.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ChatDbService {

    private static final Logger logger = LoggerFactory.getLogger(ChatDbService.class);

    private static ChatRepo chatRepo;

    public List<Chat> getLocalChats() {
        try {
            String currentUserId = UserSession.getUserId();
            if (currentUserId != null) {
                return chatRepo.findChatsByUserId(currentUserId);
            }
            return List.of();
        } catch (Exception e) {
            logger.error("Error retrieving local chats: {}", e.getMessage(), e);
            return List.of();
        }
    }


    public String findPrivateChat(String contactId) {
        List<Chat> chats = getLocalChats();

        for (Chat chat : chats) {
            if (chat.getChatType() == ChatType.GROUP) {
                continue;
            }
            if (chat.getParticipantIds().contains(contactId)) {
                return chat.getChatId();
            }
        }
        return null;
    }

    public String findGroupChat(String chatName) {
        List<Chat> chats = getLocalChats();

        for (Chat chat : chats) {
            if (chat.getChatType() == ChatType.SINGLE) {
                continue;
            }
            if (chat.getChatName().equals(chatName)) {
                return chat.getChatId();
            }
        }
        return null;
    }

    public LocalDateTime getLastChatTimestamp() {
        try {
            Chat lastChat = chatRepo.getLatestChat();
            return lastChat != null ? lastChat.getCreatedAt() : null;
        } catch (Exception e) {
            logger.error("Error retrieving last chat timestamp: {}", e.getMessage(), e);
            return null;
        }
    }

    public void addNewChats(List<ChatDTO> newChats) {
        try {
            logger.info("Adding {} new chats to local database", newChats.size());

            for (ChatDTO chatDTO : newChats) {
                // Check if chat already exists to avoid duplicates
                Optional<Chat> existingChat = chatRepo.findById(chatDTO.getChatId());
                if (existingChat.isEmpty()) {
                    Chat chat = new Chat(
                            chatDTO.getChatId(),
                            chatDTO.getChatType(),
                            chatDTO.getChatName(),
                            chatDTO.getCreatorId(),
                            chatDTO.getParticipantIds(),
                            chatDTO.getCreatedAt()
                    );
                    chatRepo.save(chat);
                }
            }

            logger.info("Successfully added {} new chats to local database", newChats.size());
        } catch (Exception e) {
            logger.error("Error adding new chats to local database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add new chats to local database", e);
        }
    }
}
