package com.MarinGallien.JavaChatApp.java_chat_app.Database;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.MessageDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.ChatParticipant;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.MessageRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.MessageType;
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
@Import(MessageDbService.class)
public class MessageDbServiceTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageDbService messageDbService;

    @Autowired
    private MessageRepo messageRepo;

    private User user1;
    private User user2;
    private User user3;
    private Chat chat;

    @BeforeEach
    void setUp() {
        user1 = new User("alice", "alice@test.com", "password1");
        user2 = new User("bob", "bob@test.com", "password2");
        user3 = new User("charlie", "charlie@test.com", "password3");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        chat = new Chat("test-chat-123", ChatType.SINGLE);
        entityManager.persistAndFlush(chat);

        // Add users as chat participants
        ChatParticipant participant1 = new ChatParticipant(chat, user1);
        ChatParticipant participant2 = new ChatParticipant(chat, user2);
        entityManager.persistAndFlush(participant1);
        entityManager.persistAndFlush(participant2);
    }

    // ==========================================================================
    // SAVE MESSAGE TESTS
    // ==========================================================================

    @Test
    void saveMessage_ValidInputs_SavesMessage() {
        // When
        Message savedMessage = messageDbService.saveMessage(user1.getUserId(), chat.getChatId(), "Hello World!");

        // Then
        assertNotNull(savedMessage);
        assertEquals("Hello World!", savedMessage.getContent());
        assertEquals(MessageType.TEXT_MESSAGE, savedMessage.getMessageType());
        assertEquals(user1.getUserId(), savedMessage.getSender().getUserId());
        assertEquals(chat.getChatId(), savedMessage.getChat().getChatId());
        assertTrue(messageRepo.existsById(savedMessage.getMessageId()));
    }

    @Test
    void saveMessage_NonExistentUser_ReturnsNull() {
        // When
        Message savedMessage = messageDbService.saveMessage("fake_id", chat.getChatId(), "Hello World!");

        // Then
        assertNull(savedMessage);
    }

    @Test
    void saveMessage_NonExistentChat_ReturnsNull() {
        // When
        Message savedMessage = messageDbService.saveMessage(user1.getUserId(), "fake_chat_id", "Hello World!");

        // Then
        assertNull(savedMessage);
    }

    @Test
    void saveMessage_UserNotInChat_ReturnsNull() {
        // When
        Message savedMessage = messageDbService.saveMessage(user3.getUserId(), chat.getChatId(), "Hello World!");

        // Then
        assertNull(savedMessage);
    }

    // ==========================================================================
    // GET CHAT MESSAGES TESTS
    // ==========================================================================

    @Test
    void getChatMessages_ValidInputs_ReturnsMessagesList() {
        // Given
        Message message1 = messageDbService.saveMessage(user1.getUserId(), chat.getChatId(), "First message");
        Message message2 = messageDbService.saveMessage(user2.getUserId(), chat.getChatId(), "Second message");
        Message message3 = messageDbService.saveMessage(user1.getUserId(), chat.getChatId(), "Third message");

        assertNotNull(message1);
        assertNotNull(message2);
        assertNotNull(message3);

        // When
        List<Message> messages = messageDbService.getChatMessages(user1.getUserId(), chat.getChatId());

        // Then
        assertNotNull(messages);
        assertEquals(3, messages.size());

        // Messages should be ordered chronologically (ASC)
        assertEquals("First message", messages.get(0).getContent());
        assertEquals("Second message", messages.get(1).getContent());
        assertEquals("Third message", messages.get(2).getContent());
    }

    @Test
    void getChatMessages_NonExistentUser_ReturnsEmptyList() {
        // Given
        messageDbService.saveMessage(user1.getUserId(), chat.getChatId(), "Test message");

        // When
        List<Message> messages = messageDbService.getChatMessages("fake_id", chat.getChatId());

        // Then
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    void getChatMessages_NonExistentChat_ReturnsEmptyList() {
        // Given
        messageDbService.saveMessage(user1.getUserId(), chat.getChatId(), "Test message");

        // When
        List<Message> messages = messageDbService.getChatMessages(user1.getUserId(), "fake_chat_id");

        // Then
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    void getChatMessages_UserNotInChat_ReturnsEmptyList() {
        // Given
        messageDbService.saveMessage(user1.getUserId(), chat.getChatId(), "Test message");

        // When
        List<Message> messages = messageDbService.getChatMessages(user3.getUserId(), chat.getChatId());

        // Then
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    void getChatMessages_EmptyChat_ReturnsEmptyList() {
        // When
        List<Message> messages = messageDbService.getChatMessages(user1.getUserId(), chat.getChatId());

        // Then
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    void getChatMessages_OnlyUserMessagesReturned() {
        // Given - create another chat with different users
        Chat otherChat = new Chat("other-chat-456", ChatType.SINGLE);
        entityManager.persistAndFlush(otherChat);

        ChatParticipant participant3 = new ChatParticipant(otherChat, user2);
        ChatParticipant participant4 = new ChatParticipant(otherChat, user3);
        entityManager.persistAndFlush(participant3);
        entityManager.persistAndFlush(participant4);

        // Save messages in both chats
        Message message1 = messageDbService.saveMessage(user1.getUserId(), chat.getChatId(), "Message in first chat");
        Message message2 = messageDbService.saveMessage(user2.getUserId(), otherChat.getChatId(), "Message in second chat");

        assertNotNull(message1);
        assertNotNull(message2);

        // When
        List<Message> firstChatMessages = messageDbService.getChatMessages(user1.getUserId(), chat.getChatId());
        List<Message> secondChatMessages = messageDbService.getChatMessages(user2.getUserId(), otherChat.getChatId());

        // Then
        assertEquals(1, firstChatMessages.size());
        assertEquals("Message in first chat", firstChatMessages.get(0).getContent());

        assertEquals(1, secondChatMessages.size());
        assertEquals("Message in second chat", secondChatMessages.get(0).getContent());
    }
}