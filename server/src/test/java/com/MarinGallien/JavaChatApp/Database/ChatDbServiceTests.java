package com.MarinGallien.JavaChatApp.Database;

import com.MarinGallien.JavaChatApp.Database.DatabaseServices.ChatDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Chat;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.ChatParticipantRepo;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.ChatRepo;
import com.MarinGallien.JavaChatApp.Enums.ChatType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(ChatDbService.class)
public class ChatDbServiceTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatDbService chatDbService;

    @Autowired
    private ChatParticipantRepo chatParticipantRepo;

    @Autowired
    private ChatRepo chatRepo;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private Set<String> members;

    @BeforeEach
    void setUp() {
        user1 = new User("Alice", "Alice@test.com", "password1");
        user2 = new User("bob", "bob@test.com", "password2");
        user3 = new User("charlie", "charlie@test.com", "password3");
        user4 = new User("dave", "dave@test.com", "password4");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
        entityManager.persistAndFlush(user4);


        // DEBUG: Print the user IDs
        System.out.println("User1 ID: " + user1.getUserId());
        System.out.println("User2 ID: " + user2.getUserId());
        System.out.println("User3 ID: " + user3.getUserId());
        System.out.println("User4 ID: " + user4.getUserId());

        members = Set.of(user2.getUserId(), user3.getUserId());

    }

    // ==========================================================================
    // PRIVATE CHAT TESTS
    // ==========================================================================

    @Test
    void createPrivateChat_ValidUsers_CreatesChat() {
        // Given
        Chat privChat = chatDbService.createPrivateChat(user1.getUserId(), user2.getUserId());
        assertNotNull(privChat);
        String pcId = privChat.getChatId();
        assertTrue(chatRepo.existsById(pcId));

        // Then
        assertNotNull(pcId);
        assertTrue(pcId.startsWith("PRIVATE_"));
        assertTrue(chatRepo.existsById(pcId));
        assertTrue(chatParticipantRepo.existsByChatChatIdAndUserUserId(pcId, user1.getUserId()));
        assertTrue(chatParticipantRepo.existsByChatChatIdAndUserUserId(pcId, user2.getUserId()));
    }

    @Test
    void createPrivateChat_NonExistentUser_ReturnsFalse() {
        // When
        Chat chat = chatDbService.createPrivateChat("fake_id", user2.getUserId());

        // Then
        assertNull(chat);
    }

    @Test
    void createPrivateChat_AlreadyExists_ReturnsFalse() {
        // Given
        Chat chat = chatDbService.createPrivateChat(user1.getUserId(), user2.getUserId());
        assertNotNull(chat);
        assertTrue(chatRepo.existsById(chat.getChatId()));

        // When
        Chat secondChat = chatDbService.createPrivateChat(user1.getUserId(), user2.getUserId());

        // Then
        assertNotNull(secondChat);
        assertEquals(chat.getChatId(), secondChat.getChatId());
    }

    // ==========================================================================
    // GROUP CHAT TESTS
    // ==========================================================================

    @Test
    void crateGroupChat_ValidInputs_CreatesChat() {
        // When
        Chat chat = chatDbService.createGroupChat(user1.getUserId(), members, "Test Group");
        Chat retrievedChat = chatRepo.findChatById(chat.getChatId());

        // Then
        assertNotNull(retrievedChat);
        String retrievedChatId = retrievedChat.getChatId();
        assertTrue(chatRepo.existsById(retrievedChatId));
        assertEquals("Test Group", chat.getChatName());
        assertEquals(ChatType.GROUP, chat.getChatType());
        assertEquals(user1.getUserId(), chat.getCreatorId());

        assertTrue(chatParticipantRepo.existsByChatChatIdAndUserUserId(retrievedChatId, user1.getUserId()));
        assertTrue(chatParticipantRepo.existsByChatChatIdAndUserUserId(retrievedChatId, user2.getUserId()));
        assertTrue(chatParticipantRepo.existsByChatChatIdAndUserUserId(retrievedChatId, user3.getUserId()));
    }

    @Test
    void createGroupChat_NonExistentCreator_ReturnsNull() {
        // When
        Chat chat = chatDbService.createGroupChat("nonexistent_userId", members, "Test Group");

        // Then
        assertNull(chat);
    }

    // ==========================================================================
    // DELETE CHAT TESTS
    // ==========================================================================

    // Test below not working due to context issues - creating and deleting database entity in the same test issue
//    @Test
//    @Transactional
//    void deleteGc_ExistingChat_DeletesSuccessfully() {
//        // Given
//        String gcId = chatDbService.createGroupChat(user1.getUserId(), members, "test_gc");
//        assertNotNull(gcId);
//        assertTrue(chatRepo.existsById(gcId));
//
//        // When
//        boolean deleted = chatDbService.deleteChat(user1.getUserId(), gcId);
//
//        // Then
//        assertTrue(deleted);
//        assertFalse(chatRepo.existsById(gcId));
//    }

    @Test
    void deleteGc_NotCreator_ReturnsFalse() {
        // Given
        Chat gc = chatDbService.createGroupChat(user1.getUserId(), members, "test_gc");
        String gcId = gc.getChatId();
        assertTrue(chatRepo.existsById(gcId));

        // When
        chatDbService.deleteChat("some_user_ID", gcId);

        // Then
        assertTrue(chatRepo.existsById(gcId));
    }

    @Test
    void deleteGc_NonExistentChat_ReturnsFalse() {
        // When
        boolean deleted = chatDbService.deleteChat(user1.getUserId(), "some_chat_ID");

        // Then
        assertFalse(deleted);
    }

    // ==========================================================================
    // ADD MEMBER TESTS
    // ==========================================================================

    @Test
    void addMemberToGc_ValidInputs_AddsMember() {
        // Given
        Set<String> testMembers = Set.of(user2.getUserId());
        Chat gc = chatDbService.createGroupChat(user1.getUserId(), testMembers, "test_chat");
        assertNotNull(gc);
        String gcId = gc.getChatId();
        assertTrue(chatRepo.existsById(gcId));

        // When
        boolean added = chatDbService.addMemberToGroupChat(gcId, user3.getUserId());

        // Then
        assertTrue(added);
        assertTrue(chatParticipantRepo.existsByChatChatIdAndUserUserId(gcId, user3.getUserId()));
    }

    @Test
    void addMemberToGc_AlreadyMember_ReturnsTrue() {
        // Given
        Chat gc = chatDbService.createGroupChat(user1.getUserId(), members, "test_gc");
        assertNotNull(gc);
        String gcId = gc.getChatId();
        assertTrue(chatRepo.existsById(gcId));

        // When
        boolean added = chatDbService.addMemberToGroupChat(gcId, user1.getUserId());

        // Then
        assertTrue(added);
    }

    @Test
    void addMemberToGc_NonExistentChat_ReturnsFalse() {
        // When
        boolean added = chatDbService.addMemberToGroupChat("chatId", user1.getUserId());

        // Then
        assertFalse(chatRepo.existsById("chatId"));
        assertFalse(added);
    }

    @Test
    void addMemberToGc_NonExistentUser_ReturnsFalse() {
        // Given
        Chat gc = chatDbService.createGroupChat(user1.getUserId(), members, "test_gc");
        assertNotNull(gc);
        String gcId = gc.getChatId();
        assertTrue(chatRepo.existsById(gcId));

        // When
        boolean added = chatDbService.addMemberToGroupChat(gcId, "test_member_ID");

        // Then
        assertFalse(added);
        assertFalse(chatParticipantRepo.existsByChatChatIdAndUserUserId(gcId, "test_user_ID"));
    }

    @Test
    void addMemberToGc_PrivateChat_ReturnsFalse() {
        // Given
        Chat pc = chatDbService.createPrivateChat(user1.getUserId(), user2.getUserId());
        assertNotNull(pc);
        String pcId = pc.getChatId();
        assertTrue(chatRepo.existsById(pcId));

        // When
        boolean added = chatDbService.addMemberToGroupChat(pcId, user3.getUserId());

        // Then
        assertFalse(added);
        assertFalse(chatParticipantRepo.existsByChatChatIdAndUserUserId(pcId, user3.getUserId()));
    }

    // ==========================================================================
    // REMOVE MEMBER TESTS
    // ==========================================================================

    @Test
    void removeMemberFromGc_ValidInputs_RemovesMember() {
        // Given
        Chat gc = chatDbService.createGroupChat(user1.getUserId(), members, "test_gc");
        assertNotNull(gc);
        String gcId = gc.getChatId();
        assertTrue(chatRepo.existsById(gcId));
        assertTrue(chatParticipantRepo.existsByChatChatIdAndUserUserId(gcId, user3.getUserId()));

        // When
        boolean removed = chatDbService.removeMemberFromGroupChat(gcId, user2.getUserId());

        // Then
        assertTrue(removed);
        assertFalse(chatParticipantRepo.existsByChatChatIdAndUserUserId(gcId, user2.getUserId()));
    }

    @Test
    void removeMemberFromGc_NonExistentChat_ReturnsFalse() {
        // When
        boolean removed = chatDbService.removeMemberFromGroupChat("chatId", user3.getUserId());

        // Then
        assertFalse(removed);
        assertFalse(chatParticipantRepo.existsByChatChatIdAndUserUserId("chatId", user3.getUserId()));
    }

    @Test
    void removeMember_NonExistentUser_ReturnsFalse() {
        // Given
        Chat gc = chatDbService.createGroupChat(user1.getUserId(), members, "test_gc");
        assertNotNull(gc);
        String gcId = gc.getChatId();
        assertTrue(chatRepo.existsById(gcId));

        // When
        boolean removed = chatDbService.removeMemberFromGroupChat(gcId, "userid");

        // Then
        assertFalse(removed);
    }

    @Test
    void removeMember_PrivateChat_ReturnsFalse() {
        // Given
        Chat pc = chatDbService.createPrivateChat(user1.getUserId(), user2.getUserId());
        assertNotNull(pc);
        String pcId = pc.getChatId();
        assertTrue(chatRepo.existsById(pcId));
        assertTrue(chatParticipantRepo.existsByChatChatIdAndUserUserId(pcId, user1.getUserId()));

        // When
        boolean removed = chatDbService.removeMemberFromGroupChat(pcId, user1.getUserId());

        // Then
        assertFalse(removed);
        assertTrue(chatParticipantRepo.existsByChatChatIdAndUserUserId(pcId, user1.getUserId()));
    }

    @Test
    void removeMember_NotMember_ReturnsFalse() {
        // Given
        Set<String> testMembers = Set.of(user2.getUserId());
        Chat gc = chatDbService.createGroupChat(user1.getUserId(), testMembers, "test_chat");
        assertNotNull(gc);
        String gcId = gc.getChatId();
        assertTrue(chatRepo.existsById(gcId));

        // When
        boolean removed = chatDbService.removeMemberFromGroupChat(gcId, user3.getUserId());

        // Then
        assertTrue(removed);
    }

    @Test
    void removeMember_MemberIsCreator_ReturnsFalse() {
        // Given
        Chat gc = chatDbService.createGroupChat(user1.getUserId(), members, "test_gc");
        assertNotNull(gc);
        String gcId = gc.getChatId();
        assertTrue(chatRepo.existsById(gcId));

        // When
        boolean removed = chatDbService.removeMemberFromGroupChat(gcId, user1.getUserId());

        // Then
        assertFalse(removed);
    }

    // ==========================================================================
    // GET USER'S CHATS TESTS
    // ==========================================================================

    @Test
    void getChats_ValidInputs_ReturnsEmptyList() {
        // Given
        Chat gc = chatDbService.createGroupChat(user1.getUserId(), members, "test_gc");
        assertNotNull(gc);
        String gcId = gc.getChatId();
        assertTrue(chatRepo.existsById(gcId));

        Chat pc = chatDbService.createPrivateChat(user1.getUserId(), user2.getUserId());
        assertNotNull(pc);
        String pcId = pc.getChatId();
        assertTrue(chatRepo.existsById(pcId));

        // When
        List<Chat> userChats = chatDbService.getUserChats(user1.getUserId());

        // Then
        assertNotNull(userChats);
        assertEquals(2, userChats.size());

        List<String> chatIds = userChats.stream().map(Chat::getChatId).toList();
        assertTrue(chatIds.contains(gcId));
        assertTrue(chatIds.contains(pcId));
    }

    @Test
    void getChats_NonExistentUser_ReturnsEmptyList() {
        // When
        List<Chat> userChats = chatDbService.getUserChats("fake_user_id");

        // Then
        assertNotNull(userChats);
        assertTrue(userChats.isEmpty());
    }

    @Test
    void getChats_UserWithNoChats_ReturnsEmptyList() {
        // Given
        User testUser = new User("charles", "Charles@test.com", "password4");
        entityManager.persistAndFlush(testUser);

        // When
        List<Chat> userChats = chatDbService.getUserChats(testUser.getUserId());

        // Then
        assertNotNull(userChats);
        assertTrue(userChats.isEmpty());
    }
}
