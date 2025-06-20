package com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.ChatParticipant;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.ChatParticipantRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.ChatRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.UserRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ChatDbService {

    private static Logger logger = LoggerFactory.getLogger(ChatDbService.class);

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ChatParticipantRepo chatParticipantRepo;

    public String createPrivateChat(String userId1, String userId2) {
        try {
            // Check if IDs exist
            if (!userRepo.existsById(userId1) || !userRepo.existsById(userId2)) {
                logger.warn("Cannot create private chat: One or both user IDs do not exist");
                return null;
            }

            // Manually generate private chat ID to avoid duplicate private chats
            String privateChatId = generateChatId(userId1, userId2);

            // Check if private chat already exists
            if (chatRepo.existsById(privateChatId)) {
                logger.warn("Private chat already exists between user {} and {}: chat ID {}", userId1, userId2, privateChatId);
                return null;
            }

            // Create and save private chat
            Chat chat = new Chat(privateChatId, ChatType.SINGLE);
            chatRepo.save(chat);

            // Add both users as participants
            User user1 = userRepo.findUserById(userId1);
            User user2 = userRepo.findUserById(userId2);

            ChatParticipant participant1 = new ChatParticipant(chat, user1);
            ChatParticipant participant2 = new ChatParticipant(chat, user2);

            chatParticipantRepo.save(participant1);
            chatParticipantRepo.save(participant2);

            logger.info("Successfully created private chat between user {} and {}: chat ID {}", userId1, userId2, privateChatId);

            return privateChatId;

        } catch (Exception e) {
            logger.error("Failed to create private chat: {}", e.getMessage());
            return null;
        }
    }

    public String createGroupChat(String creatorId, Set<String> participants, String groupName) {
        try {
            if (!userRepo.existsById(creatorId)) {
                logger.warn("Cannot create chat: user {} does not exist", creatorId);
                return null;
            }

            // Create and save chat
            Chat gc = new Chat(ChatType.GROUP, groupName, creatorId);
            chatRepo.save(gc);

            // Create and save as chat participant
            User creator = userRepo.findUserById(creatorId);
            ChatParticipant creatorParticipant = new ChatParticipant(gc, creator);
            chatParticipantRepo.save(creatorParticipant);

            // Validate each member, create participant and add to gc
            for (String id : participants) {

                if (!userRepo.existsById(id)) {
                    logger.warn("Cannot create group chat: user {} does not exist", id);
                    return null;
                }

                // Create and save as chat participant
                User user = userRepo.findUserById(id);
                ChatParticipant participant = new ChatParticipant(gc, user);
                chatParticipantRepo.save(participant);
            }

            return gc.getChatId();

        } catch (Exception e) {
            logger.error("Error creating group chat: {}", e.getMessage());
            return null;
        }
    }

    public boolean deleteChat(String creatorId, String chatId) {
        try {
            // make sure chat exists
            if (!chatRepo.existsById(chatId)) {
                logger.warn("No chat to delete: chat {} does not exist", chatId);
                return false;
            }

            // Make sure creatorId is same as chat's creator ID
            if (!chatRepo.findChatById(chatId).getCreatorId().equals(creatorId)) {
                logger.warn("Cannot delete chat: user {} was not chat creator", creatorId);
                return false;
            }

            // Save the number of chats deleted (should only be 1)
            int deletedCount = chatRepo.deleteByChatId(chatId.trim());

            // Check that something was deleted
            if (deletedCount <= 0) {
                logger.warn("Failed to delete chat {}: no rows affected", chatId);
                return false;
            }

            logger.info("Successfully deleted chat {}", chatId);
            return true;

        } catch (Exception e) {
            logger.error("Failed to delete chat: {}", e.getMessage());
            return false;
        }
    }

    public boolean addMemberToGroupChat(String chatId, String userId) {
        try {
            // Make sure both chat and user exist
            if (!userRepo.existsById(userId) || !chatRepo.existsById(chatId)) {
                logger.warn("Cannot add member to group chat: user ID or chat ID does not exist");
                return false;
            }

            // Check that chat is a group chat
            Chat chat = chatRepo.findChatById(chatId);
            if (chat.getChatType() != ChatType.GROUP) {
                logger.warn("Cannot add member because this chat is not a group chat");
                return false;
            }

            // Check that user is not already a chat member
            if (chatParticipantRepo.existsByChatChatIdAndUserUserId(chatId.trim(), userId.trim())) {
                logger.info("User {} is already a member of chat {}", userId, chatId);
                return true;
            }

            // Add user as participant
            User user = userRepo.findUserById(userId);
            ChatParticipant participant = new ChatParticipant(chat, user);
            chatParticipantRepo.save(participant);

            logger.info("Successfully added user {} to group chat {}", userId, chatId);
            return true;

        } catch (Exception e) {
            logger.error("Error adding user to group chat: {}", e.getMessage());
            return false;
        }
    }

    public boolean removeMemberFromGroupChat(String chatId, String userId) {
        try {
            // Make sure both chat and user exist
            if (!userRepo.existsById(userId) || !chatRepo.existsById(chatId)) {
                logger.warn("Cannot remove member to group chat: user ID or chat ID does not exist");
                return false;
            }

            // Check that chat is a group chat
            Chat chat = chatRepo.findChatById(chatId);
            if (chat.getChatType() != ChatType.GROUP) {
                logger.warn("Cannot remove member because this chat is not a group chat");
                return false;
            }

            // Check that user is a chat member
            if (!chatParticipantRepo.existsByChatChatIdAndUserUserId(chatId.trim(), userId.trim())) {
                logger.info("Cannot remove member: user {} is not a member of chat {}", userId, chatId);
                return true;
            }

            // Check that user is not creator
            if (userId.equals(chatRepo.findChatById(chatId).getCreatorId())) {
                logger.warn("Cannot remove member: user {} is chat creator", userId);
                return false;
            }

            // Remove user from chat
            int deletedCount = chatParticipantRepo.removeByChatChatIdAndUserUserId(chatId, userId);

            // Check that something was deleted
            if (deletedCount <= 0) {
                logger.warn("Failed to remove user {} from chat {}: no rows affected", userId, chatId);
                return false;
            }

            logger.info("Successfully removed user {} from chat {}", userId, chatId);
            return true;

        } catch (Exception e) {
            logger.error("Failed to remove user from chat: {}", e.getMessage());
            return false;
        }
    }

    public List<Chat> getUserChats(String userId) {
        try {
            if (!userRepo.existsById(userId)) {
                logger.warn("Cannot retrieve list of user's chats: user does not exist");
                return List.of();
            }

            // Retrieve chats from database
            return chatParticipantRepo.findChatsByUserUserId(userId);

        } catch (Exception e) {
            logger.error("Failed to retrieve list of user's chats: {}", e.getMessage());
            return List.of();
        }
    }

    private String generateChatId(String userId1, String userId2) {
        String[] sortedIds = {userId1, userId2};
        Arrays.sort(sortedIds);
        return "PRIVATE_" + sortedIds[0] + "_" + sortedIds[1];
    }
}
