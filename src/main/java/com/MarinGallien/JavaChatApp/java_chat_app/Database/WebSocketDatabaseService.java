package com.MarinGallien.JavaChatApp.java_chat_app.Database;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.*;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.OnlineStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WebSocketDatabaseService {

    // Parameters:

    private static final Logger logger = LoggerFactory.getLogger(WebSocketDatabaseService.class);

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private ChatParticipantRepo chatParticipantRepo;

    @Autowired
    private ContactRepo contactRepo;


    // Save a message to the database
    public Message saveMessage(Message message) {
        try {
            // Perform null checks
            if (message == null) {
                logger.warn("Cannot save message: message is null");
                return null;
            }

            if (message.getSender() == null) {
                logger.warn("Cannot save message: sender is null");
                return null;
            }

            if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                logger.warn("Cannot save message: content is null");
                return null;
            }

            // Save message to database
            Message savedMessage = messageRepo.save(message);
            logger.info("Successfully saved message with ID: {} in chat {}",
                    savedMessage.getMessageId(), savedMessage.getChat().getChatId());

            return savedMessage;
        } catch (Exception e) {
            logger.error("Error saving message to the database", e);
            return null;
        }
    }

    // Update user status in database
    public boolean saveStatus(String userId, OnlineStatus status) {
        try {
            // Perform null checks
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Cannot save status: user ID is null");
                return false;
            }

            if (status == null) {
                logger.warn("Cannot save status: online status is null");
                return false;
            }

            // Make sure user exists in database
            Optional<User> userOptional = userRepo.findById(userId.trim());
            if (userOptional == null) {
                logger.warn("Cannot save status: User with ID {} not found", userId);
                return false;
            }

            // Extract user object from optional, set status, and save to database
            User user = userOptional.get();
            OnlineStatus previousStatus = user.getStatus();
            user.setStatus(status);

            userRepo.save(user);

            logger.info("Updated status for user {} from {} to {}", userId, previousStatus, status);

            return true;
        } catch (Exception e) {
            logger.error("Error saving user status for userId: {}", userId, e);
            return false;
        }
    }

    // Retrieve all chat-user mappings in a List of objects array where [0] = chatId and [1] = userId
    @Transactional(readOnly = true)
    public List<Object[]> getAllChatParticipantMappings() {
        try {
            List<Object[]> mappings = chatParticipantRepo.findAllChatUserMappings();
            logger.info("Retrieved {} chat-participant mappings from database", mappings.size());
            return mappings;
        } catch (Exception e) {
            logger.warn("Error retrieving and chat-participant mappings from database", e);
            return List.of();
        }
    }

    // Retrieves the user IDs of all contacts for a given user
    @Transactional(readOnly = true)
    public List<String> getContacts(String userId) {
        try {
            // Perform null checks
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("Cannot get contacts: user ID is null or empty");
                return List.of();
            }

            // Retrieve list of contact IDs
            List<String> contactIds = contactRepo.findContactUserIdsByUserId(userId.trim());
            logger.info("Retrieved {} contacts for user {}", contactIds.size(), userId);

            return contactIds;
        } catch (Exception e) {
            logger.warn("Error retrieving contacts for user ID {}", userId, e);
            return List.of();
        }
    }

    public boolean userExists(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return false;
            }
            return userRepo.existsById(userId);
        } catch (Exception e) {
            logger.error("Error checking if user {} exists", userId, e);
            return false;
        }
    }

    public boolean isUserInChat(String userId, String chatId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return false;
            }
            return chatParticipantRepo.existsByChatIdAndUserId(userId, chatId);
        } catch (Exception e) {
            logger.error("Error checking if user {} is in room {}", userId, chatId);
            return false;
        }
    }

    public boolean roomExists(String chatId) {
        try {
            // Validate input
            if (chatId == null || chatId.trim().isEmpty()) {
                logger.warn("Cannot check if room exists: chat ID is null or empty");
                return false;
            }

            // Check if chat exists in database
            return chatRepo.existsById(chatId.trim());

        } catch (Exception e) {
            logger.error("Error checking if room {} exists", chatId, e);
            return false;
        }
    }
}

