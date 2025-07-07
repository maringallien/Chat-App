package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.ChatDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatCreated;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatDeleted;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.MemberAddedToChat;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.MemberRemovedFromChat;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ChatManager {
    private static final Logger logger = LoggerFactory.getLogger(ChatManager.class);

    // Thread safe map to store chatID:Set<userID>
    private final Map<String, Chat> chats = new ConcurrentHashMap<>();

    @Autowired
    private ChatDbService chatDbService;

    // Constructor
    public ChatManager(){}

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

    // Creates a chat of 2 members
    public String createPrivateChat(String userId1, String userId2) {
        // Validate input parameters
        if (!validateId(userId1)){
            logger.warn("Cannot create private chat: userID1 is null or empty");
            return null;
        }

        if (!validateId(userId1)){
            logger.warn("Cannot create private chat: userID2 is null or empty");
            return null;
        }

        if (userId1.trim().equals(userId2.trim())){
            logger.warn("Cannot create private chat: userID1 and userID2 are the same");
            return null;
        }

        // Create a private chat ID for single user chats to avoid duplicates
        String privateChatId = genPrivateChatId(userId1, userId2);

        // Check if the chat already exists
        if (chatExists(privateChatId)){
            logger.debug("A private chat already exists between user {} and {}: {}", userId1, userId2, privateChatId);
            return privateChatId;
        }

        // Create the new chat
        Chat privateChat = new Chat(privateChatId);

        // Add users to the chat
        privateChat.addMember(userId1);
        privateChat.addMember(userId2);

        // Add chat to chats map
        chats.put(privateChatId, privateChat);

        logger.info("Created a private chat {} between user {} and {}", privateChatId, userId1, userId2);
        return privateChatId;
    }

    // Creates a chat of >3 members
    public String createGroupChat(String creatorId, Set<String> initialMembers) {
        // Validate input parameters
        if (!validateId(creatorId)){
            logger.warn("Cannot create group chat: creator ID is null or empty");
            return null;
        }

        // Validate set of members
        if (initialMembers  == null || initialMembers.isEmpty()){
            logger.warn("Cannot create group chat: initial members list is null or empty");
            return null;
        }

        // Create the chat and add creator to it
        Chat groupChat = new Chat();
        groupChat.addMember(creatorId.trim());

        // Add each member to chat and make sure creator ID is not one of them. Set prevents duplicates internally
        for (String userID : initialMembers){
            if (validateId(userID) && !userID.trim().equals(creatorId.trim())){
                groupChat.addMember(userID.trim());
            } else {
                logger.warn("User ID {} could not be added to chat because ID was null or empty", userID);
            }
        }

        // Create chat and add to chats map
        String groupChatId = groupChat.getChatId();
        chats.put(groupChatId, groupChat);

        logger.info("Created group chat {} with creator {} and {} initial members",
                groupChatId, creatorId, initialMembers.size());

        return groupChatId;
    }

    // Deletes a single chat
    public boolean deleteChat(String chatId) {
        if (!validateId(chatId)){
            logger.warn("Could not delete chat: chat ID is null or empty");
            return false;
        }


        Chat removedChat = chats.remove(chatId.trim());
        removedChat.clearChat();

        if (removedChat != null) {
            logger.info("chat {} with {} participants was successfully removed", chatId, removedChat.getMembersCount());
            return true;
        } else {
            logger.warn("chat {} could not be removed because it does not exist", chatId);
            return false;
        }
    }

    // NEED ADD USER TO CHAT METHOD

    // NEED REMOVE USER FROM CHAT METHOD

    public Set<String> getChatParticipants(String chatId) {
        return chats.get(chatId).getMembers();
    }

    private String genPrivateChatId(String userId1, String userId2) {
        String[] sortedIDs = {userId1, userId2};
        Arrays.sort(sortedIDs);
        return "PRIVATE_" + sortedIDs[0] + "_" + sortedIDs[1];
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
