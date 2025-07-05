package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ChatManager {
    private static final Logger logger = LoggerFactory.getLogger(ChatManager.class);

    // Thread safe map to store chatID:Set<userID>
    private final Map<String, Chat> chats = new ConcurrentHashMap<>();

    // Constructor
    public ChatManager(){}

    // CHAT MANAGER SHOULD QUERY DATABASE UPON STARTUP TO LOAD ALL EXISTING CHATS AND PARTICIPANTS
    // CHAT MANAGER WILL NEED EVENT LISTENERS FOR:
    //  . CHAT CREATION
    //  . CHAT DELETION
    //  . USER JOINING CHAT
    //  . USER LEAVING CHAT

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

        logger.info("Created a private chat {} between user {} and {}", privateChatId, userID1, userID2);
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
