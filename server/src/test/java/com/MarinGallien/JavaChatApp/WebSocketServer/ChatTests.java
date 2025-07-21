package com.MarinGallien.JavaChatApp.WebSocketServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChatTests {

    private Chat chat;

    @BeforeEach
    void setUp() {
        chat = new Chat("test-chat-123");
    }

    @Test
    void addMember_ValidUserId_returnsTrue() {
        // Given
        boolean result = chat.addMember("user1");

        // Then
        assertTrue(result);
        assertTrue(chat.hasMember("user1"));
        assertEquals(1, chat.getMembersCount());
    }

    @Test
    void addMember_DuplicateUserId_ReturnsFalse() {
        // Given
        chat.addMember("user1");

        // When
        boolean result = chat.addMember("user1");

        // Then
        assertFalse(result);
        assertEquals(1, chat.getMembersCount());
    }

    @Test
    void removeMember_ExistingUserId_ReturnsTrue() {
        // Given
        chat.addMember("user1");

        // When
        boolean result = chat.removeMember("user1");

        // Then
        assertTrue(result);
        assertFalse(chat.hasMember("user1"));
        assertEquals(0, chat.getMembersCount());
    }

    @Test
    void removeMember_AbsentUserId_ReturnsFalse() {
        // When
        boolean result = chat.removeMember("user1");

        // Then
        assertFalse(result);
        assertEquals(0, chat.getMembersCount());
    }

    @Test
    void isEmpty_NoMembers_ReturnsTrue() {
        // When
        boolean result = chat.isEmpty();

        // Then
        assertTrue(result);
        assertEquals(0, chat.getMembersCount());
    }

    @Test
    void isEmpty_WithMembers_ReturnsFalse() {
        // Given
        chat.addMember("user1");

        // When
        boolean result = chat.isEmpty();

        // Then
        assertFalse(result);
    }

}
