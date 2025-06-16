package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChatManagerTests {
    private ChatManager chatManager;

    @BeforeEach
    void setUp() {
        chatManager = new ChatManager();
    }

    @Test
    void createPrivateChat_ValidUsers_ReturnsPrivateChatId() {
        // When
        String chatId = chatManager.createPrivateChat("user1", "user2");

        // Then
        assertNotNull(chatId);
        assertTrue(chatId.startsWith("PRIVATE"));
        assertTrue(chatManager.chatExists(chatId));

    }

    @Test
    void createPrivateChat_SameUserOrder_ReturnsSameChatId() {
        // When
        String chatId1 = chatManager.createPrivateChat("user1", "user2");
        String chatId2 = chatManager.createPrivateChat("user1", "user2");

        // Then
        assertEquals(chatId1, chatId2);
    }

    @Test
    void createGroupChat_ValidUsers_ReturnsGroupChatId() {
        // Given
        Set<String> members = Set.of("user2", "user3");

        // When
        String chatId = chatManager.createGroupChat("user1", members);

        // Then
        assertNotNull(chatId);
        assertTrue(chatManager.chatExists(chatId));
        assertEquals(3, chatManager.getChatParticipants(chatId).size());
    }

    @Test
    void deleteChat_ExistingChat_ReturnsTrue() {
        // Given
        String chatId = chatManager.createPrivateChat("user1", "user2");

        // When
        boolean result = chatManager.deleteChat(chatId);

        // Then
        assertTrue(result);
        assertFalse(chatManager.chatExists(chatId));
    }

    @Test
    void chatExists_NonExistentChat_ReturnsFalse() {
        // When
        boolean result = chatManager.chatExists("nonexistent");

        // Then
        assertFalse(result);
    }
}
