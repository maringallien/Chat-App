package com.MarinGallien.JavaChatApp.java_chat_app.Database;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.WebsocketMessages.WebSocketMessage;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.ChatParticipant;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.Contact;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.*;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.OnlineStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(WebSocketDatabaseService.class)
class DatabaseIntegrationTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WebSocketDatabaseService webSocketDatabaseService;

    @Autowired
    private ChatParticipantRepo chatParticipantRepo;

    @Autowired
    private ContactRepo contactRepo;

    private User user1;
    private User user2;
    private User user3;
    private Chat chat1;

    @BeforeEach
    void setUp() {
        // Create test users
        user1 = new User("user1", "user1@test.com", "password1");
        user2 = new User("user2", "user2@test.com", "password2");
        user3 = new User("user3", "user3@test.com", "password3");

        // Persist test users in database
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        // Create and persist test chat
        chat1 = new Chat(ChatType.SINGLE);
        entityManager.persistAndFlush(chat1);

        // Set up chat participants - user1 and user2 are in chat1, user3 is not
        ChatParticipant cp1 = new ChatParticipant(chat1, user1);
        ChatParticipant cp2 = new ChatParticipant(chat1, user2);
        entityManager.persistAndFlush(cp1);
        entityManager.persistAndFlush(cp2);

        // Set up contacts - user1 has user2 and user3 as contacts
        Contact contact1 = new Contact(user1, user2);
        Contact contact2 = new Contact(user1, user3);
        entityManager.persistAndFlush(contact1);
        entityManager.persistAndFlush(contact2);

        entityManager.clear();
    }

    // ==========================================================================
    // TESTING BUSINESS LOGIC IN WebSocketDatabaseService
    // ==========================================================================

    @Test
    void saveMessage_ValidMessage_SavesSuccessfully() {
        // Given - valid message from user1 to chat1
        WebSocketMessage wsMessage = new WebSocketMessage(
                user1.getUserId(),
                chat1.getChatId(),
                "Hello from integration test",
                user2.getUserId()
        );

        // When
        Message savedMessage = webSocketDatabaseService.saveMessage(wsMessage);

        // Then
        assertNotNull(savedMessage);
        assertEquals("Hello from integration test", savedMessage.getContent());
        assertEquals(user1.getUserId(), savedMessage.getSender().getUserId());
        assertEquals(chat1.getChatId(), savedMessage.getChat().getChatId());
    }

    @Test
    void saveMessage_UserNotInChat_ReturnsNull() {
        // Given - user3 is NOT in chat1
        WebSocketMessage wsMessage = new WebSocketMessage(
                user3.getUserId(),
                chat1.getChatId(),
                "This should fail",
                user1.getUserId()
        );

        // When
        Message savedMessage = webSocketDatabaseService.saveMessage(wsMessage);

        // Then - Your business logic should reject this
        assertNull(savedMessage);
    }

    @Test
    void saveMessage_NonExistentUser_ReturnsNull() {
        // Given - fake user ID
        WebSocketMessage wsMessage = new WebSocketMessage(
                "fake-user-id",
                chat1.getChatId(),
                "This should fail",
                user1.getUserId()
        );

        // When
        Message savedMessage = webSocketDatabaseService.saveMessage(wsMessage);

        // Then - Your business logic should reject this
        assertNull(savedMessage);
    }

    @Test
    void saveMessage_NonExistentChat_ReturnsNull() {
        // Given - fake chat ID
        WebSocketMessage wsMessage = new WebSocketMessage(
                user1.getUserId(),
                "fake-chat-id",
                "This should fail",
                user2.getUserId()
        );

        // When
        Message savedMessage = webSocketDatabaseService.saveMessage(wsMessage);

        // Then - Your business logic should reject this
        assertNull(savedMessage);
    }

    @Test
    void saveStatus_ValidUser_UpdatesStatus() {
        // Given - user starts as OFFLINE
        assertEquals(OnlineStatus.OFFLINE, user1.getStatus());

        // When
        boolean result = webSocketDatabaseService.saveStatus(user1.getUserId(), OnlineStatus.ONLINE);

        // Then
        assertTrue(result);

        // Verify the status was actually updated in database
        entityManager.flush();
        entityManager.clear();
        User updatedUser = entityManager.find(User.class, user1.getUserId());
        assertEquals(OnlineStatus.ONLINE, updatedUser.getStatus());
    }

    @Test
    void saveStatus_NonExistentUser_ReturnsFalse() {
        // When
        boolean result = webSocketDatabaseService.saveStatus("fake-user-id", OnlineStatus.ONLINE);

        // Then - Your business logic should reject this
        assertFalse(result);
    }

    @Test
    void getContacts_ValidUser_ReturnsContacts() {
        // When
        List<String> contacts = webSocketDatabaseService.getContacts(user1.getUserId());

        // Then - user1 should have user2 and user3 as contacts
        assertEquals(2, contacts.size());
        assertTrue(contacts.contains(user2.getUserId()));
        assertTrue(contacts.contains(user3.getUserId()));
    }

    @Test
    void getContacts_NonExistentUser_ReturnsEmptyList() {
        // When
        List<String> contacts = webSocketDatabaseService.getContacts("fake-user-id");

        // Then
        assertTrue(contacts.isEmpty());
    }

    @Test
    void getAllChatParticipantMappings_ReturnsCorrectMappings() {
        // When
        List<Object[]> mappings = webSocketDatabaseService.getAllChatParticipantMappings();

        // Then - should have 2 mappings for our single chat
        assertEquals(2, mappings.size());

        // Verify structure and content
        for (Object[] mapping : mappings) {
            assertEquals(2, mapping.length);
            assertEquals(chat1.getChatId(), mapping[0]); // All mappings should be for chat1
            assertTrue(mapping[1].equals(user1.getUserId()) || mapping[1].equals(user2.getUserId()));
        }
    }

    @Test
    void userExists_ExistingUser_ReturnsTrue() {
        // When
        boolean exists = webSocketDatabaseService.userExists(user1.getUserId());

        // Then
        assertTrue(exists);
    }

    @Test
    void userExists_NonExistentUser_ReturnsFalse() {
        // When
        boolean exists = webSocketDatabaseService.userExists("fake-user-id");

        // Then
        assertFalse(exists);
    }

    @Test
    void chatExists_ExistingChat_ReturnsTrue() {
        // When
        boolean exists = webSocketDatabaseService.chatExists(chat1.getChatId());

        // Then
        assertTrue(exists);
    }

    @Test
    void chatExists_NonExistentChat_ReturnsFalse() {
        // When
        boolean exists = webSocketDatabaseService.chatExists("fake-chat-id");

        // Then
        assertFalse(exists);
    }

    @Test
    void isUserInChat_UserInChat_ReturnsTrue() {
        // When
        boolean inChat = webSocketDatabaseService.isUserInChat(user1.getUserId(), chat1.getChatId());

        // Then
        assertTrue(inChat);
    }

    @Test
    void isUserInChat_UserNotInChat_ReturnsFalse() {
        // When - user3 is not in chat1
        boolean inChat = webSocketDatabaseService.isUserInChat(user3.getUserId(), chat1.getChatId());

        // Then
        assertFalse(inChat);
    }

    // ==========================================================================
    // TESTING CUSTOM REPOSITORY QUERIES
    // ==========================================================================

    @Test
    void chatParticipantRepo_FindAllChatUserMappings_ReturnsCorrectStructure() {
        // When - testing your custom @Query method
        List<Object[]> mappings = chatParticipantRepo.findAllChatUserMappings();

        // Then
        assertFalse(mappings.isEmpty());

        // Verify each mapping has correct structure: [chatId, userId]
        for (Object[] mapping : mappings) {
            assertEquals(2, mapping.length);
            assertTrue(mapping[0] instanceof String); // chatId
            assertTrue(mapping[1] instanceof String); // userId
        }
    }

    @Test
    void chatParticipantRepo_ExistsByChatChatIdAndUserUserId_WorksCorrectly() {
        // When - testing your custom method
        boolean userInChat = chatParticipantRepo.existsByChatChatIdAndUserUserId(chat1.getChatId(), user1.getUserId());
        boolean userNotInChat = chatParticipantRepo.existsByChatChatIdAndUserUserId(chat1.getChatId(), user3.getUserId());

        // Then
        assertTrue(userInChat);
        assertFalse(userNotInChat);
    }

    @Test
    void contactRepo_FindContactUserIdsByUserId_ReturnsCorrectContacts() {
        // When - testing your custom @Query method
        List<String> contacts = contactRepo.findContactUserIdsByUserId(user1.getUserId());

        // Then
        assertEquals(2, contacts.size());
        assertTrue(contacts.contains(user2.getUserId()));
        assertTrue(contacts.contains(user3.getUserId()));
    }
}