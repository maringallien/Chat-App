package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.MessageDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private static Logger logger = LoggerFactory.getLogger(MessageDbService.class);

    private final MessageDbService messageDbService;

    public MessageService(MessageDbService messageDbService) {
        this.messageDbService = messageDbService;
    }

    public Message saveMessage(String userId, String chatId, String content) {
        try {
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

        } catch (Exception e) {
            logger.error("Failed to same message: {}", e.getMessage());
            return null;
        }
    }

    public List<Message> getChatMessages(String userId, String chatId) {
        try {
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

            return messages;

        } catch (Exception e) {
            logger.error("Failed to retrieve messages: {}", e.getMessage());
            return List.of();
        }
    }

    private boolean validateId(String Id) {
        return Id != null && !Id.trim().isEmpty();
    }
}
