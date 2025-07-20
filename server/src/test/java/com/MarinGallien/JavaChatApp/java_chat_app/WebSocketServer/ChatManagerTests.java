package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import Database.DatabaseServices.ChatDbService;
import EventSystem.Events.ChatEvents.ChatCreated;
import EventSystem.Events.ChatEvents.ChatDeleted;
import EventSystem.Events.ChatEvents.MemberAddedToChat;
import EventSystem.Events.ChatEvents.MemberRemovedFromChat;
import WebSocketServer.ChatManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatManagerTests {

    @Mock
    private ChatDbService chatDbService;

    private ChatManager chatManager;

    @BeforeEach
    void setUp() {
        chatManager = new ChatManager(chatDbService);
        ReflectionTestUtils.setField(chatManager, "chatDbService", chatDbService);

        when(chatDbService.getAllChatParticipantMappings()).thenReturn(List.of());
        chatManager.initializeChatManager();
    }

    // ==========================================================================
    // UTILITY METHOD TESTS
    // ==========================================================================

    @Test
    void chatExists_NonExistentChat_ReturnsFalse() {
        // When
        boolean result = chatManager.chatExists("nonexistent");

        // Then
        assertFalse(result);
    }

    // ==========================================================================
    // EVENT LISTENER TESTS
    // ==========================================================================

    @Test
    void handleChatCreated_ValidEvent_CreatesChat() {
        // Given
        ChatCreated event = new ChatCreated("chat123", Set.of("user1", "user2"));

        // When
        chatManager.handleChatCreated(event);

        // Then
        assertTrue(chatManager.chatExists("chat123"));
    }

    @Test
    void handleChatCreated_ExistingChat_UpdatesMembers() {
        // Given
        ChatCreated initialEvent = new ChatCreated("chat123", Set.of("user1"));
        chatManager.handleChatCreated(initialEvent);

        // When
        ChatCreated updateEvent = new ChatCreated("chat123", Set.of("user1", "user2"));
        chatManager.handleChatCreated(updateEvent);

        // Then
        assertTrue(chatManager.chatExists("chat123"));
    }

    @Test
    void handleChatDeleted_ValidEvent_RemovesChat() {
        // Given
        ChatCreated createEvent = new ChatCreated("chat123", Set.of("user1", "user2"));
        chatManager.handleChatCreated(createEvent);

        // When
        ChatDeleted deleteEvent = new ChatDeleted("chat123");
        chatManager.handleChatDeleted(deleteEvent);

        // Then
        assertFalse(chatManager.chatExists("chat123"));
    }

    @Test
    void handleChatDeleted_NonExistentChat_DoesNothing() {
        // When
        ChatDeleted event = new ChatDeleted("nonexistent_chat");
        chatManager.handleChatDeleted(event);

        // Then - should not throw exception
        assertTrue(true);
    }

    @Test
    void handleMemberAddedToChat_ExistingChat_AddsMember() {
        // Given
        ChatCreated createEvent = new ChatCreated("chat123", Set.of("user1"));
        chatManager.handleChatCreated(createEvent);

        // When
        MemberAddedToChat event = new MemberAddedToChat("user2", "chat123");
        chatManager.handleMemberAddedToChat(event);

        // Then
        assertTrue(chatManager.chatExists("chat123"));
    }

    @Test
    void handleMemberAddedToChat_NonExistentChat_CreatesChat() {
        // When
        MemberAddedToChat event = new MemberAddedToChat("user1", "new_chat");
        chatManager.handleMemberAddedToChat(event);

        // Then
        assertTrue(chatManager.chatExists("new_chat"));
    }

    @Test
    void handleMemberRemovedFromChat_ValidEvent_RemovesMember() {
        // Given
        ChatCreated createEvent = new ChatCreated("chat123", Set.of("user1", "user2"));
        chatManager.handleChatCreated(createEvent);

        // When
        MemberRemovedFromChat event = new MemberRemovedFromChat("user1", "chat123");
        chatManager.handleMemberRemovedFromChat(event);

        // Then
        assertTrue(chatManager.chatExists("chat123"));
    }

    @Test
    void handleMemberRemovedFromChat_NonExistentChat_DoesNothing() {
        // When
        MemberRemovedFromChat event = new MemberRemovedFromChat("user1", "nonexistent_chat");
        chatManager.handleMemberRemovedFromChat(event);

        // Then - should not throw exception
        assertTrue(true);
    }

    // ==========================================================================
    // INITIALIZATION TESTS
    // ==========================================================================

    @Test
    void initializeChatManager_WithExistingData_LoadsChats() {
        // Given
        List<Object[]> mockMappings = List.of(
                new Object[]{"chat1", "user1"},
                new Object[]{"chat1", "user2"},
                new Object[]{"chat2", "user3"}
        );

        ChatManager freshManager = new ChatManager(chatDbService);
        ReflectionTestUtils.setField(freshManager, "chatDbService", chatDbService);
        when(chatDbService.getAllChatParticipantMappings()).thenReturn(mockMappings);

        // When
        freshManager.initializeChatManager();

        // Then
        assertTrue(freshManager.chatExists("chat1"));
        assertTrue(freshManager.chatExists("chat2"));
    }

    @Test
    void initializeChatManager_EmptyDatabase_NoChatsLoaded() {
        // Given
        ChatManager freshManager = new ChatManager(chatDbService);
        ReflectionTestUtils.setField(freshManager, "chatDbService", chatDbService);
        when(chatDbService.getAllChatParticipantMappings()).thenReturn(List.of());

        // When
        freshManager.initializeChatManager();

        // Then
        assertFalse(freshManager.chatExists("any_chat_id"));
    }
}