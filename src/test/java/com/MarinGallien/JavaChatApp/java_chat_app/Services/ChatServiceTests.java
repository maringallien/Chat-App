package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.ChatDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.EventBusService;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatCreated;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatDeleted;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.MemberAddedToChat;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.MemberRemovedFromChat;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTests {

    @Mock
    private ChatDbService chatDbService;

    @Mock
    private EventBusService eventBus;

    @InjectMocks
    private ChatService chatService;

    private Chat testPrivateChat;
    private Chat testGroupChat;
    private String user1Id = "user1";
    private String user2Id = "user2";
    private String user3Id = "user3";
    private String privateChatId = "private-chat-123";
    private String groupChatId = "group-chat-456";

    @BeforeEach
    void setUp() {
        testPrivateChat = new Chat(privateChatId, ChatType.SINGLE);
        testGroupChat = new Chat(groupChatId, ChatType.GROUP);
    }

    // ==========================================================================
    // CREATE PRIVATE CHAT TESTS
    // ==========================================================================

    @Test
    void createPrivateChatRequest_ValidInputs_CreatesChat() {
        // Given
        when(chatDbService.createPrivateChat(user1Id, user2Id)).thenReturn(testPrivateChat);

        // When
        Chat result = chatService.createPrivateChat(user1Id, user2Id);

        // Then
        assertNotNull(result);
        assertEquals(privateChatId, result.getChatId());

        verify(chatDbService).createPrivateChat(user1Id, user2Id);
        verify(eventBus).publishEvent(any(ChatCreated.class));
    }

    @Test
    void createPrivateChat_InvalidParameters_ReturnsNull() {
        // Test all null parameters
        assertNull(chatService.createPrivateChat(null, user2Id));
        assertNull(chatService.createPrivateChat(user1Id, null));

        // Test all empty parameters
        assertNull(chatService.createPrivateChat("", user2Id));
        assertNull(chatService.createPrivateChat(user1Id, ""));

        // Test all whitespace parameters
        assertNull(chatService.createPrivateChat("   ", user2Id));
        assertNull(chatService.createPrivateChat(user1Id, "   "));

        // Test same user IDs (business rule)
        assertNull(chatService.createPrivateChat(user1Id, user1Id));

        // Verify no database calls or events were made
        verify(chatDbService, never()).createPrivateChat(any(), any());
        verify(eventBus, never()).publishEvent(any());
    }

    @Test
    void createPrivateChat_DatabaseFailure_ReturnsNull() {
        // Given
        when(chatDbService.createPrivateChat(user1Id, user2Id)).thenReturn(null);

        // When
        Chat result = chatService.createPrivateChat(user1Id, user2Id);

        // Then
        assertNull(result);
        verify(chatDbService).createPrivateChat(user1Id, user2Id);
        verify(eventBus, never()).publishEvent(any());
    }

    // ==========================================================================
    // CREATE GROUP CHAT TESTS
    // ==========================================================================

    @Test
    void createGroupChatRequest_ValidInputs_CreatesChat() {
        // Given
        Set<String> memberIds = Set.of(user2Id, user3Id);
        String chatName = "Test Group";
        when(chatDbService.createGroupChat(user1Id, memberIds, chatName)).thenReturn(testGroupChat);

        // When
        Chat result = chatService.createGroupChat(user1Id, memberIds, chatName);

        // Then
        assertNotNull(result);
        assertEquals(groupChatId, result.getChatId());

        verify(chatDbService).createGroupChat(user1Id, memberIds, chatName);
        verify(eventBus).publishEvent(any(ChatCreated.class));
    }

    @Test
    void createGroupChat_InvalidParameters_ReturnsNull() {
        Set<String> memberIds = Set.of(user2Id, user3Id);

        // Test all null parameters
        assertNull(chatService.createGroupChat(null, memberIds, "Test Group"));
        assertNull(chatService.createGroupChat(user1Id, null, "Test Group"));
        assertNull(chatService.createGroupChat(user1Id, memberIds, null));

        // Test all empty creator ID
        assertNull(chatService.createGroupChat("", memberIds, "Test Group"));
        assertNull(chatService.createGroupChat(user1Id, Set.of(), "Test Group"));
        assertNull(chatService.createGroupChat(user1Id, memberIds, ""));

        // Test all whitespace parameters
        assertNull(chatService.createGroupChat("   ", memberIds, "Test Group"));
        assertNull(chatService.createGroupChat(user1Id, memberIds, "   "));

        // Verify no database calls or events were made
        verify(chatDbService, never()).createGroupChat(any(), any(), any());
        verify(eventBus, never()).publishEvent(any());
    }

    @Test
    void createGroupChat_DatabaseFailure_ReturnsNull() {
        // Given
        Set<String> memberIds = Set.of(user2Id, user3Id);
        when(chatDbService.createGroupChat(user1Id, memberIds, "Test Group")).thenReturn(null);

        // When
        Chat result = chatService.createGroupChat(user1Id, memberIds, "Test Group");

        // Then
        assertNull(result);
        verify(chatDbService).createGroupChat(user1Id, memberIds, "Test Group");
        verify(eventBus, never()).publishEvent(any());
    }

    // ==========================================================================
    // DELETE CHAT TESTS
    // ==========================================================================

    @Test
    void deleteChatRequest_ValidInputs_DeletesChat() {
        // Given
        when(chatDbService.deleteChat(user1Id, groupChatId)).thenReturn(true);

        // When
        boolean result = chatService.deleteChat(user1Id, groupChatId);

        // Then
        assertTrue(result);
        verify(chatDbService).deleteChat(user1Id, groupChatId);
        verify(eventBus).publishEvent(any(ChatDeleted.class));
    }

    @Test
    void deleteChat_InvalidParameters_ReturnsFalse() {
        // Test all null parameters
        assertFalse(chatService.deleteChat(null, groupChatId));
        assertFalse(chatService.deleteChat(user1Id, null));

        // Test all empty parameters
        assertFalse(chatService.deleteChat("", groupChatId));
        assertFalse(chatService.deleteChat(user1Id, ""));

        // Test all whitespace parameters
        assertFalse(chatService.deleteChat("   ", groupChatId));
        assertFalse(chatService.deleteChat(user1Id, "   "));

        // Verify no database calls or events were made
        verify(chatDbService, never()).deleteChat(any(), any());
        verify(eventBus, never()).publishEvent(any());
    }

    @Test
    void deleteChat_DatabaseFailure_ReturnsFalse() {
        // Given
        when(chatDbService.deleteChat(user1Id, groupChatId)).thenReturn(false);

        // When
        boolean result = chatService.deleteChat(user1Id, groupChatId);

        // Then
        assertFalse(result);
        verify(chatDbService).deleteChat(user1Id, groupChatId);
        verify(eventBus, never()).publishEvent(any());
    }

    // ==========================================================================
    // ADD MEMBER TESTS
    // ==========================================================================

    @Test
    void addMemberRequest_ValidInputs_AddsMember() {
        // Given
        when(chatDbService.addMemberToGroupChat(groupChatId, user3Id)).thenReturn(true);

        // When
        boolean result = chatService.addMember(user1Id, user3Id, groupChatId);

        // Then
        assertTrue(result);
        verify(chatDbService).addMemberToGroupChat(groupChatId, user3Id);
        verify(eventBus).publishEvent(any(MemberAddedToChat.class));
    }

    @Test
    void addMember_InvalidParameters_ReturnsFalse() {
        // Test all null parameters
        assertFalse(chatService.addMember(null, user3Id, groupChatId));
        assertFalse(chatService.addMember(user1Id, null, groupChatId));
        assertFalse(chatService.addMember(user1Id, user3Id, null));

        // Test all empty parameters
        assertFalse(chatService.addMember("", user3Id, groupChatId));
        assertFalse(chatService.addMember(user1Id, "", groupChatId));
        assertFalse(chatService.addMember(user1Id, user3Id, ""));

        // Test all whitespace parameters
        assertFalse(chatService.addMember("   ", user3Id, groupChatId));
        assertFalse(chatService.addMember(user1Id, "   ", groupChatId));
        assertFalse(chatService.addMember(user1Id, user3Id, "   "));

        // Verify no database calls or events were made
        verify(chatDbService, never()).addMemberToGroupChat(any(), any());
        verify(eventBus, never()).publishEvent(any());
    }

    @Test
    void addMember_DatabaseFailure_ReturnsFalse() {
        // Given
        when(chatDbService.addMemberToGroupChat(groupChatId, user3Id)).thenReturn(false);

        // When
        boolean result = chatService.addMember(user1Id, user3Id, groupChatId);

        // Then
        assertFalse(result);
        verify(chatDbService).addMemberToGroupChat(groupChatId, user3Id);
        verify(eventBus, never()).publishEvent(any());
    }

    // ==========================================================================
    // REMOVE MEMBER TESTS
    // ==========================================================================

    @Test
    void removeMemberRequest_ValidInputs_RemovesMember() {
        // Given
        when(chatDbService.removeMemberFromGroupChat(groupChatId, user3Id)).thenReturn(true);

        // When
        boolean result = chatService.removeMember(user1Id, user3Id, groupChatId);

        // Then
        assertTrue(result);
        verify(chatDbService).removeMemberFromGroupChat(groupChatId, user3Id);
        verify(eventBus).publishEvent(any(MemberRemovedFromChat.class));
    }

    @Test
    void removeMember_InvalidParameters_ReturnsFalse() {
        // Test all null parameters
        assertFalse(chatService.removeMember(null, user3Id, groupChatId));
        assertFalse(chatService.removeMember(user1Id, null, groupChatId));
        assertFalse(chatService.removeMember(user1Id, user3Id, null));

        // Test all empty parameters
        assertFalse(chatService.removeMember("", user3Id, groupChatId));
        assertFalse(chatService.removeMember(user1Id, "", groupChatId));
        assertFalse(chatService.removeMember(user1Id, user3Id, ""));

        // Test all whitespace parameters
        assertFalse(chatService.removeMember("   ", user3Id, groupChatId));
        assertFalse(chatService.removeMember(user1Id, "   ", groupChatId));
        assertFalse(chatService.removeMember(user1Id, user3Id, "   "));

        // Verify no database calls or events were made
        verify(chatDbService, never()).removeMemberFromGroupChat(any(), any());
        verify(eventBus, never()).publishEvent(any());
    }

    @Test
    void removeMember_DatabaseFailure_ReturnsFalse() {
        // Given
        when(chatDbService.removeMemberFromGroupChat(groupChatId, user3Id)).thenReturn(false);

        // When
        boolean result = chatService.removeMember(user1Id, user3Id, groupChatId);

        // Then
        assertFalse(result);
        verify(chatDbService).removeMemberFromGroupChat(groupChatId, user3Id);
        verify(eventBus, never()).publishEvent(any());
    }

    // ==========================================================================
    // GET USER CHATS TESTS
    // ==========================================================================

    @Test
    void getUserChatsRequest_ValidInputs_ReturnsChats() {
        // Given
        List<Chat> userChats = List.of(testPrivateChat, testGroupChat);
        when(chatDbService.getUserChats(user1Id)).thenReturn(userChats);

        // When
        List<Chat> result = chatService.getUserChats(user1Id);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testPrivateChat));
        assertTrue(result.contains(testGroupChat));

        verify(chatDbService).getUserChats(user1Id);
    }

    @Test
    void getUserChatsRequest_InvalidParameters_ReturnsNull() {
        // Test null user ID
        assertNull(chatService.getUserChats(null));

        // Test empty user ID
        assertNull(chatService.getUserChats(""));

        // Test whitespace user ID
        assertNull(chatService.getUserChats("   "));

        // Verify no database calls were made
        verify(chatDbService, never()).getUserChats(any());
    }

    @Test
    void getUserChatsRequest_DatabaseReturnsEmpty_ReturnsNull() {
        // Given
        when(chatDbService.getUserChats(user1Id)).thenReturn(List.of());

        // When
        List<Chat> result = chatService.getUserChats(user1Id);

        // Then
        assertNull(result);
        verify(chatDbService).getUserChats(user1Id);
    }
}