package com.MarinGallien.JavaChatApp.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.Database.JPARepos.MessageRepo;
import com.MarinGallien.JavaChatApp.JPAEntities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MessageDbService {

    private static final Logger logger = LoggerFactory.getLogger(MessageDbService.class);

    private final MessageRepo messageRepo;

    public MessageDbService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public void saveMessage(Message message) {
        try {
            messageRepo.save(message);
            logger.debug("Added local message {} to chat {}", message.getMessageId(), message.getChatId());
        } catch (Exception e) {
            logger.error("Error adding local message: {}", e.getMessage(), e);
        }
    }

    public List<Message> getChatMessages(String chatId) {
        try {
            return messageRepo.findByChatChatId(chatId);
        } catch (Exception e) {
            logger.error("Error retrieving chat messages: {}", e.getMessage(), e);
            return null;
        }
    }

    public LocalDateTime getLastMessageTimestamp(String chatId) {
        try {
            Message lastMessage = messageRepo.getLatestMessageForChat(chatId);
            return lastMessage != null ? lastMessage.getSentAt() : null;
        } catch (Exception e) {
            logger.error("Error retrieving last message timestamp for chat {}: {}", chatId, e.getMessage(), e);
            return null;
        }
    }

    public void addNewMessages(List<MessageDTO> newMessages) {
        try {
            logger.info("Adding {} new messages to local database", newMessages.size());

            for (MessageDTO messageDTO : newMessages) {
                // Check if message already exists to avoid duplicates
                Optional<Message> existingMessage = messageRepo.findById(messageDTO.getMessageId());
                if (existingMessage.isEmpty()) {
                    Message message = new Message(
                            messageDTO.getMessageId(),
                            messageDTO.getSenderId(),
                            messageDTO.getChatId(),
                            messageDTO.getContent(),
                            messageDTO.getSentAt()
                    );
                    messageRepo.save(message);
                }
            }

            logger.info("Successfully added {} new messages to local database", newMessages.size());
        } catch (Exception e) {
            logger.error("Error adding new messages to local database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add new messages to local database", e);
        }
    }
}
