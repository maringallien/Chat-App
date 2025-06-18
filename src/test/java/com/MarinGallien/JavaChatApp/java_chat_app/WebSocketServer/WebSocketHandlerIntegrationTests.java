package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.ChatParticipant;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.Contact;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.MessageRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.WebSocketDatabaseService;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.*;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles
@Import({WebSocketDatabaseService.class, StatusManager.class, ChatManager.class})
public class WebSocketHandlerIntegrationTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private WebSocketDatabaseService databaseService;
    @Autowired
    private StatusManager statusManager;
    @Autowired
    private MessageRepo messageRepo;

    @Mock
    private ChatManager chatManager;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private WebSocketHandler webSocketHandler;
    private User user1;
    private User user2;
    private Chat chat1;
    private WebSocketMessage testMessage;

    @BeforeEach
    void setUp() {
        // Create real webSocketHandler with real dependencies except messaging template
        webSocketHandler = new WebSocketHandler(databaseService, statusManager, chatManager, messagingTemplate);

        // Create and persist users in database
        user1 = new User("alice", "alice@test.com", "password");
        user2 = new User("bob", "bob@test.com", "password");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // Create and persist test chat
        chat1 = new Chat(ChatType.SINGLE);
        entityManager.persistAndFlush(chat1);

        // Make users chat participants
        ChatParticipant cp1 = new ChatParticipant(chat1, user1);
        ChatParticipant cp2 = new ChatParticipant(chat1, user2);
        entityManager.persistAndFlush(cp1);
        entityManager.persistAndFlush(cp2);

        // Make users contacts of each other
        Contact contact1 = new Contact(user1, user2);
        Contact contact2 = new Contact(user2, user1);
        entityManager.persistAndFlush(contact1);
        entityManager.persistAndFlush(contact2);

        entityManager.clear();

        // Add chat to chat manager
        chatManager.createPrivateChat(user1.getUserId(), user2.getUserId());

        // Create test message
        testMessage = new WebSocketMessage(user1.getUserId(), chat1.getChatId(), "Hello World", user2.getUserId());
    }

    @Test
    void handleTextMessage_ValidMessage_SavesAndProcesses() {
        // Given
        statusManager.setUserOnline(user1.getUserId());
        statusManager.setUserOnline(user2.getUserId());

        // Mock chatManager to return the participants for the chat
        Set<String> chatParticipants = Set.of(user1.getUserId(), user2.getUserId());
        when(chatManager.getChatParticipants(chat1.getChatId())).thenReturn(chatParticipants);

        // When
        webSocketHandler.handleTextMessage(chat1.getChatId(), testMessage);

        // Then - verify message was saved to database
        Message savedMessage = databaseService.saveMessage(testMessage);
        assertNotNull(savedMessage);
        assertTrue(messageRepo.existsById(savedMessage.getMessageId()));

        // Verify it exists in database
        Message foundMessage = messageRepo.findById(savedMessage.getMessageId()).orElse(null);
        assertNotNull(foundMessage);
        assertEquals("Hello World", foundMessage.getContent());

        // Verify message was sent to recipients (user 1 sender and user 2 recipient)
        verify(messagingTemplate).convertAndSendToUser(user1.getUserId(), "/queue/messages", testMessage);
        verify(messagingTemplate).convertAndSendToUser(user2.getUserId(), "/queue/messages", testMessage);
        verify(messagingTemplate, times(2)).convertAndSendToUser(anyString(), eq("/queue/messages"), eq(testMessage));
    }

    @Test
    void handleTextMessage_UserNotInChat_DoesNotSend() {
        // Given
        statusManager.setUserOnline(user1.getUserId());
        statusManager.setUserOnline(user2.getUserId());

        // Mock chatManager to return only user1 as participant
        Set<String> chatParticipants = Set.of(user1.getUserId());
        when(chatManager.getChatParticipants(chat1.getChatId())).thenReturn(chatParticipants);

        // When
        webSocketHandler.handleTextMessage(chat1.getChatId(), testMessage);

        // Then - verify message was saved to database
        Message savedMessage = databaseService.saveMessage(testMessage);
        assertNotNull(savedMessage);
        assertTrue(messageRepo.existsById(savedMessage.getMessageId()));

        // Verify it exists in database
        Message foundMessage = messageRepo.findById(savedMessage.getMessageId()).orElse(null);
        assertNotNull(foundMessage);
        assertEquals("Hello World", foundMessage.getContent());

        // Verify message was sent to recipients (user 1 sender and user 2 recipient)
        verify(messagingTemplate).convertAndSendToUser(user1.getUserId(), "/queue/messages", testMessage);
        verify(messagingTemplate, never()).convertAndSendToUser(user2.getUserId(), "/queue/messages", testMessage);
        verify(messagingTemplate, times(1)).convertAndSendToUser(anyString(), eq("/queue/messages"), eq(testMessage));
    }
}
