package com.MarinGallien.JavaChatApp.Services;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.MessageDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Message;
import com.MarinGallien.JavaChatApp.Database.Mappers.MessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private static Logger logger = LoggerFactory.getLogger(MessageDbService.class);

    private final MessageDbService messageDbService;
    private final MessageMapper messageMapper;

    public MessageService(MessageDbService messageDbService, MessageMapper messageMapper) {
        this.messageDbService = messageDbService;
        this.messageMapper = messageMapper;
    }

    public Message saveMessage(String userId, String chatId, String content) {
        if (!validateId(userId) || !validateId(chatId) || content == null || content.trim().isEmpty()) {
            logger.warn("Failed to save message: missing required fields");
            return null;
        }

        // Save message to database
        Message message = messageDbService.saveMessage(userId, chatId, content);

        // Make sure something was saved
        if (message == null) {
            logger.info("Failed to save message to the database");
        }

        logger.info("Successfully saved message to the database");
        return message;
    }

    public List<MessageDTO> getChatMessages(String userId, String chatId) {
        if (!validateId(userId) || !validateId(chatId)) {
            logger.warn("Failed to save message: sender or chat ID is null or empty");
            return List.of();
        }

        // Retrieve messages from database
        List<Message> messages = messageDbService.getChatMessages(userId, chatId);

        // Make sure something was retrieved
        if (!messages.isEmpty()) {
            logger.info("Successfully retrieved chat messages from the database");
        } else {
            logger.info("No messages were found in this chat");
        }

        // Convert to DTO and return
        return messageMapper.toDTOList(messages);
    }

    private boolean validateId(String Id) {
        return Id != null && !Id.trim().isEmpty();
    }
}
