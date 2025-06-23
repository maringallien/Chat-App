package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.MessageDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.ChatParticipant;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.ChatParticipantRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.MessageRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.UserRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.MessageEvents.SaveMessageRequest;
import io.netty.handler.codec.MessageAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public class MessageService {
    private static Logger logger = LoggerFactory.getLogger(MessageDbService.class);

    @Autowired
    MessageDbService messageDbService;

    @EventListener
    @Async("eventTaskExecutor")
    public void saveMessage(SaveMessageRequest event) {
        try {
            // Validate input parameters
            if (event == null) {
                logger.warn("Failed to save message: message is null");
                return;
            }

            if (!validateId(event.senderId()) || !validateId(event.chatId()) ||
                    event.content() == null || event.content().isEmpty()) {
                logger.warn("Failed to save message: missing required fields");
                return;
            }

            // Save message to database
            Message message = messageDbService.saveMessage(event.senderId(), event.chatId(), event.content());

            // Make sure something was saved
            if (message != null) {
                logger.info("Successfully saved message to the database");
            } else {
                logger.info("Failed to save message to the database");
            }

        } catch (Exception e) {
            logger.error("Failed to same message: {}", e.getMessage());
            return;
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
