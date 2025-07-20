package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import WebSocketServer.StatusManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatusManagerTests {

    private StatusManager statusManager;

    @BeforeEach
    void setUp() {
        statusManager = new StatusManager();
    }

    @Test
    void setUserOnline_ValidUserId_UserIsOnline() {
        // When
        statusManager.setUserOnline("user1");

        // Then
        assertTrue(statusManager.isOnline("user1"));
    }

    @Test
    void setUserOffline_OnlineUser_UserIsOffline() {
        // Given
        statusManager.setUserOnline("user1");

        // When
        statusManager.setUserOffline("user1");

        // Then
        assertFalse(statusManager.isOnline("user1"));
    }

    @Test
    void isOnline_UserNotSet_ReturnFalse() {
        // When
        boolean result = statusManager.isOnline("user1");

        // Then
        assertFalse(result);
    }

}
