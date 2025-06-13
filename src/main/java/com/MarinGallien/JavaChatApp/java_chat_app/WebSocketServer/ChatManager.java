package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
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

    // Creates a chat of 2 members
    public String createPrivateChat(String userID1, String userID2) {
        // Validate input parameters
        if (!checkId(userID1)){
            logger.warn("Cannot create private chat: userID1 is null or empty");
            return null;
        }

        if (!checkId(userID1)){
            logger.warn("Cannot create private chat: userID2 is null or empty");
            return null;
        }

        if (userID1.trim().equals(userID2.trim())){
            logger.warn("Cannot create private chat: userID1 and userID2 are the same");
            return null;
        }

        // Create a private chat ID for single user chats to avoid duplicates
        String privateChatId = genPrivateChatId(userID1, userID2);

        // Check if the chat already exists
        if (chatExists(privateChatId)){
            logger.debug("A private chat already exists between user {} and {}: {}", userID1, userID2, privateChatId);
            return privateChatId;
        }

        // Create the new chat
        Chat privateChat = new Chat(privateChatId);

        // Add users to the chat
        privateChat.addMember(userID1);
        privateChat.addMember(userID2);

        // Add chat to chats map
        chats.put(privateChatId, privateChat);

        logger.info("Created a private chat {} between user {} and {}", privateChatId, userID1, userID2);
        return privateChatId;
    }

    // Check user ID is valid
    private boolean checkId(String userID) {
        return userID != null && !userID.trim().isEmpty();
    }

    private String genPrivateChatId(String userID1, String userID2) {
        String[] sortedIDs = {userID1, userID2};
        Arrays.sort(sortedIDs);
        return "PRIVATE_" + sortedIDs[0] + "_" + sortedIDs[1];
    }

    // Creates a chat of >3 members
    public String createGroupChat(String creatorID, Set<String> initialMembers) {
        // Validate input parameters
        if (!checkId(creatorID)){
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
        groupChat.addMember(creatorID.trim());

        // Add each member to chat and make sure creator ID is not one of them. Set prevents duplicates internally
        for (String userID : initialMembers){
            if (checkId(userID) && !userID.trim().equals(creatorID.trim())){
                groupChat.addMember(userID.trim());
            } else {
                logger.warn("User ID {} could not be added to chat because ID was null or empty");
            }
        }

        // Create chat and add to chats map
        String groupChatId = groupChat.getChatId();
        chats.put(groupChatId, groupChat);

        logger.info("Created group chat {} with creator {} and {} initial members",
                groupChatId, creatorID, initialMembers.size());

        return groupChatId;
    }

    // Deletes a single chat
    public boolean deleteChat(String chatID) {
        if (!checkId(chatID)){
            logger.warn("Could not delete chat: chat ID is null or empty");
            return false;
        }


        Chat removedChat = chats.remove(chatID.trim());
        removedChat.clearChat();

        if (removedChat != null) {
            logger.info("chat {} with {} participants was successfully removed", chatID, removedChat.getMembersCount());
            return true;
        } else {
            logger.warn("chat {} could not be removed because it does not exist", chatID);
            return false;
        }
    }

    // Deletes all chats
    public boolean clearAllChats() {
        try {
            int chatCount = chats.size();
            chats.clear();
            logger.info("{} chats were deleted", chatCount);
            return true;
        } catch (Exception e) {
            logger.error("Error clearing chats", e);
            return false;
        }
    }

    // Returns a chat object corresponding to chatID
    public Chat getChat(String chatID) {
        if (!checkId(chatID)) {
            logger.warn("Could not retrieve chat because ID is null or empty");
            return null;
        }

        return chats.get(chatID.trim());
    }

    // Returns the total number of chats
    public int getTotalChats() {
        return chats.size();
    }

    // Returns a set of chat IDs
    public Set<String> getAllChatIDs() {
        return new HashSet<>(chats.keySet());
    }

    // Checks if the chat exists
    public boolean chatExists(String chatID) {
        if (!checkId(chatID)) {
            logger.warn("Could not check if chat exists because chat ID is null or empty");
            return false;
        }
        return chats.containsKey(chatID);
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return "chatManager{" +
                "total chats=" + chats.size() +
                ", chats=" + chats.keySet() +
                '}';
    }
}
