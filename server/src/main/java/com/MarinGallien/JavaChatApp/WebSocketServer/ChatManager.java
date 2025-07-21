package com.MarinGallien.JavaChatApp.WebSocketServer;

import com.MarinGallien.JavaChatApp.Database.DatabaseServices.ChatDbService;
import com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents.ChatCreated;
import com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents.ChatDeleted;
import com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents.MemberAddedToChat;
import com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents.MemberRemovedFromChat;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ChatManager {
    private static final Logger logger = LoggerFactory.getLogger(ChatManager.class);

    // Thread safe map to store chatID:Set<userID>
    private final Map<String, Chat> chats = new ConcurrentHashMap<>();
    private final ChatDbService chatDbService;

    public ChatManager(ChatDbService chatDbService) {
        this.chatDbService = chatDbService;
    }

    // ========== INITIALIZATION ==========
    @PostConstruct
    public void initializeChatManager() {
        try {
            // Retrieve all chat to participant mappings from database
            List<Object[]> chatPartMap = chatDbService.getAllChatParticipantMappings();

            // Make sure it is not empty
            if (chatPartMap.isEmpty()) {
                logger.info("No existing chats found in database. ChatManager loading complete");
                return;
            }

            Map<String, Set<String>> chatParticipants = new ConcurrentHashMap<>();

            // Group chat participants by chat ID
            for (Object[] mapping : chatPartMap) {
                String chatId = (String) mapping[0];
                String userId = (String) mapping[1];

                chatParticipants.computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet()).add(userId);
            }

            // Create chat objects and populate the chats map
            for (Map.Entry<String, Set<String>> entry : chatParticipants.entrySet()) {
                String chatId = entry.getKey();
                Set<String> participants = entry.getValue();

                Chat chat = new Chat(chatId);
                for (String userId : participants) {
                    chat.addMember(userId);
                }

                chats.put(chatId, chat);
            }

            logger.info("Successfully loaded {} participants from database into chatManager", chats.size());

        } catch (Exception e) {
            logger.error("Failed to initialize chatManager from database: {}", e.getMessage());
        }
    }


    // ========== EVENT LISTENERS ==========
    @EventListener
    @Async("eventTaskExecutor")
    public void handleChatCreated(ChatCreated event) {
        try {
            // Extract input parameters
            String chatId = event.chatId();
            Set<String> memberIds = event.memberIds();

            // Validate input parameters
            if (!validateId(chatId) || memberIds == null || memberIds.isEmpty()) {
                logger.warn("Failed to handle ChatCreated event: one or more input parameter is null or empty");
                return;
            }

            Chat chat;

            if (chatExists(chatId)) {
                logger.warn("Chat already exists, updating members to ensure consistency with database");
                // If chat already exists, just find it
                chat = chats.get(chatId);
            } else {
                logger.warn("Chat does not exists, creating new chat");
                // If chat does not exist, create a new one
                chat = new Chat(chatId);
                chats.put(chatId, chat);
            }

            // Whether a new chat is created or one is retrieved from chats map, add members to the chat
            addMembersToChat(chat, memberIds);

            logger.info("Successfully created/updated chat {} with {} members in chatManager", chatId, memberIds.size());

        } catch (Exception e) {
            logger.error("Error handling ChatCreated event: {}", e.getMessage());
        }
    }

    private void addMembersToChat(Chat chat, Set<String> members) {
        for (String memberId : members) {
            if (validateId(memberId)) {
                // If member already exists operation will fail, preventing duplicates
                chat.addMember(memberId);
            } else {
                logger.warn("Invalid member ID was not added to chat");
            }
        }
    }

    @EventListener
    @Async("eventTaskExecutor")
    public void handleChatDeleted(ChatDeleted event) {
        try {
            // Extract input parameters
            String chatId = event.chatId();

            // Validate input parameters
            if (!validateId(chatId)) {
                logger.warn("Failed to handle ChatDeleted event: chat ID is null or empty");
                return;
            }

            // Make sure the chat exists
            if (!chatExists(chatId)) {
                logger.warn("Chat {} does not exist in chatManager, nothing to delete", chatId);
            }

            // Remove the chat
            Chat removedChat = chats.remove(chatId);

            // Make sure chat was removed
            if (removedChat == null) {
                logger.warn("Failed to remove chat {} from chatManager", chatId);
                return;
            }

            logger.info("Successfully removed chat {} from chatManager", removedChat.getChatId());

        } catch (Exception e) {
            logger.error("Error handling ChatDeleted event: {}", e.getMessage());
        }
    }

    @EventListener
    @Async("eventTaskExecutor")
    public void handleMemberAddedToChat(MemberAddedToChat event) {
        try {
            // Extract input parameters
            String chatId = event.chatId();
            String userId = event.userId();

            // Validate input parameters
            if (!validateId(chatId) || !validateId(userId)) {
                logger.warn("Failed to handle MemberAddedToChat event: chat or user ID is null or empty");
                return;
            }

            // Check if chat exists
            if (!chatExists(chatId)) {
                logger.warn("Cannot add member to non-existent chat - creating new chat with ID {}", chatId);
                Chat newChat = new Chat(chatId);
                chats.put(chatId, newChat);
            }

            // Get the chat and add member
            Chat chat = chats.get(chatId);
            boolean added = chat.addMember(userId);

            if (!added) {
                logger.warn("Failed to add user {} to chat {}", userId, chatId);
                return;
            }

            logger.info("Successfully added user {} to chat {}", userId, chatId);

        } catch (Exception e) {
            logger.error("Error handling MemberAddedToChat event: {}", e.getMessage());
        }
    }

    @EventListener
    @Async("eventTaskExecutor")
    public void handleMemberRemovedFromChat(MemberRemovedFromChat event) {
        try {
            // Extract input parameters
            String chatId = event.chatId();
            String userId = event.userId();

            // Validate input parameters
            if (!validateId(chatId) || !validateId(userId)) {
                logger.warn("Failed to handle MemberRemovedFromChat event: chat or user ID is null or empty");
                return;
            }

            // Check if chat exists
            if (!chatExists(chatId)) {
                logger.warn("Cannot remove member from chat - chat {} does not exist", chatId);
                return;
            }

            // Get the chat and remove member
            Chat chat = chats.get(chatId);
            boolean removed = chat.removeMember(userId);

            if (!removed) {
                logger.warn("Failed to remove user {} to chat {}", userId, chatId);
                return;
            }

            logger.info("Successfully removed user {} to chat {}", userId, chatId);

        } catch (Exception e) {
            logger.error("Error handling MemberRemovedFromChat event: {}", e.getMessage());
        }
    }

    public Set<String> getChatParticipants(String chatId) {
        return chats.get(chatId).getMembers();
    }

    // Checks if the chat exists
    public boolean chatExists(String chatID) {
        if (!validateId(chatID)) {
            logger.warn("Could not check if chat exists because chat ID is null or empty");
            return false;
        }
        return chats.containsKey(chatID);
    }

    // Check user ID is valid
    private boolean validateId(String userId) {
        return userId != null && !userId.trim().isEmpty();
    }

}
