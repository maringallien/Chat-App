package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.ChatDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.EventBusService;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications.ChatCreated;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications.ChatDeleted;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications.MemberAddedToChat;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications.MemberRemovedFromChat;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Requests.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatDbService chatBdService;

    @Autowired
    private EventBusService eventBus;

    @EventListener
    @Async("eventTaskExecutor")
    public void handleCreatePrivateChatRequest(CreatePrivateChatRequest event) {
        try {
            // Validate input parameters
            if (!validateId(event.userId1()) || !validateId(event.userId2())) {
                logger.warn("Error processing event: a user ID is null or empty");
                return;
            }

            if (event.userId1().equals(event.userId2())) {
                logger.warn("Error processing event: both user IDs are identical");
                return;
            }

            // Create private chat in database
            String chatId = chatBdService.createPrivateChat(event.userId1(), event.userId2());

            // Make sure chat was created
            if (!validateId(chatId)) {
                logger.warn("Failed to create private chat");
                return;
            }

            // Notify chatManager
            Set<String> participants = Set.of(event.userId1(), event.userId2());
            ChatCreated createdEvent = new ChatCreated(chatId, participants);
            eventBus.publishEvent(createdEvent);

        } catch (Exception e) {
            logger.error("Error processing private chat creation request");
        }
    }

    @EventListener
    @Async("eventTaskExecutor")
    public void handleCreateGroupChatRequest(CreateGroupChatRequest event) {
        try {
            // Validate creator ID
            if (!validateId(event.creatorId())) {
                logger.warn("Error processing event: creator ID is null or empty");
                return;
            }

            // Validate member IDs list
            if (event.memberIds() == null || event.memberIds().isEmpty()) {
                logger.warn("Error processing event: list of member IDs is null or empty");
                return;
            }

            // Validate member IDs
            for (String memberId : event.memberIds()) {
                if (!validateId(memberId)) {
                    logger.warn("Error processing event: a member ID is null or empty");
                    return;
                }
            }

            // Validate chatName
            if (event.chatName() == null || event.chatName().isEmpty()) {
                logger.warn("Error processing event: chat name is null or empty");
                return;
            }

            String chatId = chatBdService.createGroupChat(event.creatorId(), event.memberIds(), event.chatName());

            // Make sure chat was created
            if (!validateId(chatId)) {
                logger.warn("Failed to create group chat");
                return;
            }

            // Create a set with all members, including creator
            Set<String> allMembersIds = new HashSet<>(event.memberIds());
            allMembersIds.add(event.creatorId());

            // Notify chatManager of new chat created
            ChatCreated createdEvent = new ChatCreated(chatId, allMembersIds);
            eventBus.publishEvent(createdEvent);

        } catch (Exception e) {
            logger.error("Error processing group chat creation request");
        }
    }

    @EventListener
    @Async("eventTaskExecutor")
    public void handleDeleteChatRequest(DeleteChatRequest event) {
        try {
            // Validate input parameters
            if (!validateId(event.creatorId()) || !validateId(event.chatId())) {
                logger.warn("Error processing event: user ID or chat ID is null or empty");
                return;
            }

            // Update database
            boolean deleted = chatBdService.deleteChat(event.creatorId(), event.chatId());

            // Make sure chat was deleted
            if (!deleted) {
                logger.warn("Failed to delete chat {}", event.chatId());
                return;
            }

            // Notify chatManager
            ChatDeleted deletedEvent = new ChatDeleted(event.chatId());
            eventBus.publishEvent(deletedEvent);

        } catch (Exception e) {
            logger.error("Error processing chat deletion request");
        }
    }

    @EventListener
    @Async("eventTaskExecutor")
    public void handleAddMemberRequest(AddMemberRequest event) {
        try {
            // Validate input parameters
            if (!validateId(event.creatorId()) || !validateId(event.userId()) || !validateId(event.chatId())) {
                logger.warn("Error processing event: creator ID, user ID, or chat ID is null or empty");
                return;
            }

            // Update database
            boolean added = chatBdService.addMemberToGroupChat(event.chatId(), event.userId());

            // Make sure member was added
            if (!added) {
                logger.warn("Failed to add user {} to chat {}", event.userId(), event.chatId());
                return;
            }

            // Notify chatManager
            MemberAddedToChat addedEvent = new MemberAddedToChat(event.userId(), event.chatId());
            eventBus.publishEvent(addedEvent);

        } catch (Exception e) {
            logger.error("Error processing member adding to chat request");
        }
    }

    @EventListener
    @Async("eventTaskExecutor")
    public void handleRemoveMemberRequest(RemoveMemberRequest event) {
        try {
            // Validate input parameters
            if (!validateId(event.creatorId()) || !validateId(event.userId()) || !validateId(event.chatId())) {
                logger.warn("Error processing event: creator ID, user ID, or chat ID is null or empty");
                return;
            }

            // Update database
            boolean removed = chatBdService.removeMemberFromGroupChat(event.chatId(), event.userId());

            // Make sure member was removed
            if (!removed) {
                logger.warn("Failed to remove member {} from chat {}", event.userId(), event.chatId());
                return;
            }

            // Notify chatManager
            MemberRemovedFromChat removedEvent = new MemberRemovedFromChat(event.userId(), event.chatId());
            eventBus.publishEvent(removedEvent);

        } catch (Exception e) {
            logger.error("Error processing member removal from chat request");
        }
    }

    // SHOULD NOT BE TAKING AN EVENT IF NOT A LISTENER
    public List<Chat> handleGetUserChatsRequest(GetUserChatsRequest event) {
        try {
            // Validate input parameters
            if (!validateId(event.userId())) {
                logger.warn("Error processing event: user ID is null or empty");
                return null;
            }

            // Retrieve list of chats
            List<Chat> chats = chatBdService.getUserChats(event.userId());

            if (chats == null || chats.isEmpty()) {
                logger.warn("No chats were found for user {}", event.userId());
                return null;
            }

            return chats;

        } catch (Exception e) {
            logger.error("Error processing chats list request");
            return null;
        }
    }

    private boolean validateId(String Id) {
        return Id != null && !Id.trim().isEmpty();
    }
}
