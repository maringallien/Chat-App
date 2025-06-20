//package com.MarinGallien.JavaChatApp.java_chat_app.Database;
//
//import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.WebSocketMessage;
//import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.WebSocketDbService;
//import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
//import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
//import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
//import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.*;
//import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;
//import com.MarinGallien.JavaChatApp.java_chat_app.Enums.MessageType;
//import com.MarinGallien.JavaChatApp.java_chat_app.Enums.OnlineStatus;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class WebSocketDbServiceTests {
//
//    @Mock
//    private MessageRepo messageRepo;
//    @Mock
//    private UserRepo userRepo;
//    @Mock
//    private ChatRepo chatRepo;
//    @Mock
//    private ChatParticipantRepo chatParticipantRepo;
//    @Mock
//    private ContactRepo contactRepo;
//
//    @InjectMocks
//    private WebSocketDbService service;
//
//    private User testUser;
//    private Chat testChat;
//    private WebSocketMessage testMessage;
//    private Message testMessageEntity;
//
//    @BeforeEach
//    void setUp() {
//        // Create test user and set ID for easier testing
//        testUser = new User("testuser", "test@email.com", "password");
//        testUser.setUserId("user123");
//
//        // Create test chat and set ID for easier testing
//        testChat = new Chat(ChatType.SINGLE);
//        testChat.setChatId("chat123");
//
//        // Create test message DTO entity
//        testMessage = new WebSocketMessage("user123", "chat123", "Hello", "user456");
//
//        // Create test message database entity
//        testMessageEntity = new Message(testUser, testChat, "Hello World", MessageType.TEXT_MESSAGE);
//        testMessageEntity.setMessageId("msg123");
//    }
//
//    @Test
//    void saveMessage_ValidMessage_ReturnsMessage() {
//        // Given
//        when(userRepo.existsById("user123")).thenReturn(true);
//        when(chatRepo.existsById("chat123")).thenReturn(true);
//        when(chatParticipantRepo.existsByChatChatIdAndUserUserId("chat123", "user123")).thenReturn(true);
//        when(chatRepo.findChatById("chat123")).thenReturn(testChat);
//        when(userRepo.findUserById("user123")).thenReturn(testUser);
//        when(messageRepo.save(any(Message.class))).thenReturn(testMessageEntity);
//
//        // When
//        Message result = service.saveMessage(testMessage);
//
//        // Then
//        assertNotNull(result);
//        assertEquals("Hello World", result.getContent());
//    }
//
//    @Test
//    void saveMessage_UserNotFound_ReturnsNull() {
//        // Given
//        when(userRepo.existsById("user123")).thenReturn(false);
//
//        // When
//        Message result = service.saveMessage(testMessage);
//
//        // Then
//        assertNull(result);
//    }
//
//    @Test
//    void saveStatus_ValidUser_ReturnsTrue() {
//        // Given
//        when(userRepo.existsById("user123")).thenReturn(true);
//        when(userRepo.findUserById("user123")).thenReturn(testUser);
//
//        // When
//        boolean result = service.saveStatus("user123", OnlineStatus.ONLINE);
//
//        // Then
//        assertTrue(result);
//        assertEquals(OnlineStatus.ONLINE, testUser.getStatus());
//    }
//
//    @Test
//    void saveStatus_NullUserId_ReturnsFalse() {
//        // When
//        boolean result = service.saveStatus(null, OnlineStatus.ONLINE);
//
//        // Then
//        assertFalse(result);
//        verify(userRepo, never()).existsById(anyString());
//    }
//
//    @Test
//    void getContacts_ValidUserId_ReturnsContacts() {
//        // Given
//        List<String> contacts = Arrays.asList("user456", "user789");
//        when(contactRepo.findContactUserIdsByUserId("user123")).thenReturn(contacts);
//
//        // When
//        List<String> result = service.getContacts("user123");
//
//        // Then
//        assertEquals(2, result.size());
//        assertTrue(result.contains("user456"));
//        assertTrue(result.contains("user789"));
//    }
//
//    @Test
//    void userExists_ExistingUser_ReturnsTrue() {
//        // Given
//        when(userRepo.existsById("user123")).thenReturn(true);
//
//        // When
//        boolean result = service.userExists("user123");
//
//        // Then
//        assertTrue(result);
//    }
//
//    @Test
//    void chatExists_ExistingChat_ReturnsTrue() {
//        // Given
//        when(chatRepo.existsById("chat123")).thenReturn(true);
//
//        // When
//        boolean result = service.chatExists("chat123");
//
//        // Then
//        assertTrue(result);
//    }
//}
