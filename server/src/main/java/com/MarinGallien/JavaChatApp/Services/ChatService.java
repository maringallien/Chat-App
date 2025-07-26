package com.MarinGallien.JavaChatApp.Services;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.ChatDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Chat;
import com.MarinGallien.JavaChatApp.EventSystem.EventBusService;
import com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents.ChatCreated;
import com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents.ChatDeleted;
import com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents.MemberAddedToChat;
import com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents.MemberRemovedFromChat;
import com.MarinGallien.JavaChatApp.Database.Mappers.ChatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatDbService chatDbService;
    private final EventBusService eventBus;
    private final ChatMapper chatMapper;

    public ChatService(ChatDbService chatDbService, EventBusService eventBus, ChatMapper chatMapper) {
        this.chatDbService = chatDbService;
        this.eventBus = eventBus;
        this.chatMapper = chatMapper;
    }

    public Chat createPrivateChat(String userId1, String userId2) {
        try {
            // Validate input parameters
            if (!validateId(userId1) || !validateId(userId2)) {
                logger.warn("Error processing event: a user ID is null or empty");
                return null;
            }

            if (userId1.equals(userId2)) {
                logger.warn("Error processing event: both user IDs are identical");
                return null;
            }

            // Create private chat in database
            Chat chat = chatDbService.createPrivateChat(userId1, userId2);

            // Make sure chat was created
            if (chat == null) {
                logger.warn("Failed to create private chat");
                return null;
            }

            // Notify chatManager
            Set<String> participants = Set.of(userId1, userId2);
            ChatCreated createdEvent = new ChatCreated(chat.getChatId(), participants);
            eventBus.publishEvent(createdEvent);

            return chat;

        } catch (Exception e) {
            logger.error("Error processing private chat creation request");
            return null;
        }
    }

    public Chat createGroupChat(String creatorId, Set<String> memberIds, String chatName) {
        try {
            // Validate creator ID
            if (!validateId(creatorId)) {
                logger.warn("Error processing event: creator ID is null or empty");
                return null;
            }

            // Validate member IDs list
            if (memberIds == null || memberIds.isEmpty()) {
                logger.warn("Error processing event: list of member IDs is null or empty");
                return null;
            }

            // Validate member IDs
            for (String memberId : memberIds) {
                if (!validateId(memberId)) {
                    logger.warn("Error processing event: a member ID is null or empty");
                    return null;
                }
            }

            // Validate chatName
            if (chatName == null || chatName.trim().isEmpty()) {
                logger.warn("Error processing event: chat name is null or empty");
                return null;
            }

            Chat chat = chatDbService.createGroupChat(creatorId, memberIds, chatName);

            // Make sure chat was created
            if (chat == null) {
                logger.warn("Failed to create group chat");
                return null;
            }

            // Create a set with all members, including creator
            Set<String> allMembersIds = new HashSet<>(memberIds);
            allMembersIds.add(creatorId);

            // Notify chatManager of new chat created
            ChatCreated createdEvent = new ChatCreated(chat.getChatId(), allMembersIds);
            eventBus.publishEvent(createdEvent);

            return chat;

        } catch (Exception e) {
            logger.error("Error processing group chat creation request");
            return null;
        }
    }

    public boolean deleteChat(String creatorId, String chatId) {
        try {
            // Validate input parameters
            if (!validateId(creatorId) || !validateId(chatId)) {
                logger.warn("Error processing event: user ID or chat ID is null or empty");
                return false;
            }

            // Update database
            boolean deleted = chatDbService.deleteChat(creatorId, chatId);

            // Make sure chat was deleted
            if (!deleted) {
                logger.warn("Failed to delete chat {}", chatId);
                return false;
            }

            // Notify chatManager
            ChatDeleted deletedEvent = new ChatDeleted(chatId);
            eventBus.publishEvent(deletedEvent);

            return deleted;

        } catch (Exception e) {
            logger.error("Error processing chat deletion request");
            return false;
        }
    }

    public boolean addMember(String creatorId, String userId, String chatId) {
        try {
            // Validate input parameters
            if (!validateId(creatorId) || !validateId(userId) || !validateId(chatId)) {
                logger.warn("Error processing event: creator ID, user ID, or chat ID is null or empty");
                return false;
            }

            // Update database
            boolean added = chatDbService.addMemberToGroupChat(chatId, userId);

            // Make sure member was added
            if (!added) {
                logger.warn("Failed to add user {} to chat {}", userId, chatId);
                return false;
            }

            // Notify chatManager
            MemberAddedToChat addedEvent = new MemberAddedToChat(userId, chatId);
            eventBus.publishEvent(addedEvent);

            return added;

        } catch (Exception e) {
            logger.error("Error processing member adding to chat request");
            return false;
        }
    }

    public boolean removeMember(String creatorId, String userId, String chatId) {
        try {
            // Validate input parameters
            if (!validateId(creatorId) || !validateId(userId) || !validateId(chatId)) {
                logger.warn("Error processing event: creator ID, user ID, or chat ID is null or empty");
                return false;
            }

            // Update database
            boolean removed = chatDbService.removeMemberFromGroupChat(chatId, userId);

            // Make sure member was removed
            if (!removed) {
                logger.warn("Failed to remove member {} from chat {}", userId, chatId);
                return false;
            }

            // Notify chatManager
            MemberRemovedFromChat removedEvent = new MemberRemovedFromChat(userId, chatId);
            eventBus.publishEvent(removedEvent);

            return removed;

        } catch (Exception e) {
            logger.error("Error processing member removal from chat request");
            return false;
        }
    }

    public List<ChatDTO> getUserChats(String userId) {
        try {
            // Validate input parameters
            if (!validateId(userId)) {
                logger.warn("Error processing event: user ID is null or empty");
                return List.of();
            }

            // Retrieve list of chats
            List<Chat> chats = chatDbService.getUserChats(userId);

            if (chats == null || chats.isEmpty()) {
                logger.warn("No chats were found for user {}", userId);
                return List.of();
            }

            // Convert to DTO and return
            return chatMapper.toDTOList(chats);

        } catch (Exception e) {
            logger.error("Error processing chats list request");
            return List.of();
        }
    }

    private boolean validateId(String Id) {
        return Id != null && !Id.trim().isEmpty();
    }
}
