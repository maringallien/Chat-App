package com.MarinGallien.JavaChatApp.java_chat_app.Database;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.FileDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.File;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.ChatParticipant;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories.FileRepo;
import com.MarinGallien.JavaChatApp.java_chat_app.Enums.ChatType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(FileDbService.class)
public class FileDbServiceTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FileDbService fileDbService;

    @Autowired
    private FileRepo fileRepo;

    private User user1;
    private User user2;
    private Chat chat;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        user1 = new User("alice", "alice@test.com", "password1");
        user2 = new User("bob", "bob@test.com", "password2");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        chat = new Chat("test-chat-123", ChatType.SINGLE);
        entityManager.persistAndFlush(chat);

        // Add users as chat participants
        ChatParticipant participant1 = new ChatParticipant(chat, user1);
        ChatParticipant participant2 = new ChatParticipant(chat, user2);
        entityManager.persistAndFlush(participant1);
        entityManager.persistAndFlush(participant2);

        // Create test file
        testFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test file content".getBytes()
        );
    }

    // ==========================================================================
    // UPLOAD FILE TESTS
    // ==========================================================================

    @Test
    void uploadFile_ValidInputs_UploadsFile() {
        // When
        File uploadedFile = fileDbService.uploadFile(user1.getUserId(), chat.getChatId(), testFile);

        // Then
        assertNotNull(uploadedFile);
        assertEquals("test.txt", uploadedFile.getFilename());
        assertEquals("text/plain", uploadedFile.getFileType());
        assertEquals(user1.getUserId(), uploadedFile.getUploader().getUserId());
        assertTrue(fileRepo.existsById(uploadedFile.getFileId()));
    }

    @Test
    void uploadFile_NonExistentUser_ReturnsNull() {
        // When
        File uploadedFile = fileDbService.uploadFile("fake_id", chat.getChatId(), testFile);

        // Then
        assertNull(uploadedFile);
    }

    @Test
    void uploadFile_NonExistentChat_ReturnsNull() {
        // When
        File uploadedFile = fileDbService.uploadFile(user1.getUserId(), "fake_chat_id", testFile);

        // Then
        assertNull(uploadedFile);
    }

    @Test
    void uploadFile_UserNotInChat_ReturnsNull() {
        // Given - create new user not in chat
        User user3 = new User("charlie", "charlie@test.com", "password3");
        entityManager.persistAndFlush(user3);

        // When
        File uploadedFile = fileDbService.uploadFile(user3.getUserId(), chat.getChatId(), testFile);

        // Then
        assertNull(uploadedFile);
    }

    @Test
    void uploadFile_NullFile_ReturnsNull() {
        // When
        File uploadedFile = fileDbService.uploadFile(user1.getUserId(), chat.getChatId(), null);

        // Then
        assertNull(uploadedFile);
    }

    @Test
    void uploadFile_EmptyFile_ReturnsNull() {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        // When
        File uploadedFile = fileDbService.uploadFile(user1.getUserId(), chat.getChatId(), emptyFile);

        // Then
        assertNull(uploadedFile);
    }

    // ==========================================================================
    // DELETE FILE TESTS
    // ==========================================================================

    @Test
    void deleteFile_ValidInputs_DeletesFile() {
        // Given
        File uploadedFile = fileDbService.uploadFile(user1.getUserId(), chat.getChatId(), testFile);
        assertNotNull(uploadedFile);
        String fileId = uploadedFile.getFileId();

        // When
        Boolean deleted = fileDbService.deleteFile(user1.getUserId(), chat.getChatId(), fileId);

        // Then
        assertTrue(deleted);
        assertFalse(fileRepo.existsById(fileId));
    }

    @Test
    void deleteFile_NonExistentUser_ReturnsFalse() {
        // Given
        File uploadedFile = fileDbService.uploadFile(user1.getUserId(), chat.getChatId(), testFile);
        assertNotNull(uploadedFile);

        // When
        Boolean deleted = fileDbService.deleteFile("fake_id", chat.getChatId(), uploadedFile.getFileId());

        // Then
        assertFalse(deleted);
        assertTrue(fileRepo.existsById(uploadedFile.getFileId()));
    }

    @Test
    void deleteFile_NonExistentFile_ReturnsFalse() {
        // When
        Boolean deleted = fileDbService.deleteFile(user1.getUserId(), chat.getChatId(), "fake_file_id");

        // Then
        assertFalse(deleted);
    }

    @Test
    void deleteFile_UserNotFileOwner_ReturnsFalse() {
        // Given
        File uploadedFile = fileDbService.uploadFile(user1.getUserId(), chat.getChatId(), testFile);
        assertNotNull(uploadedFile);

        // When - user2 tries to delete user1's file
        Boolean deleted = fileDbService.deleteFile(user2.getUserId(), chat.getChatId(), uploadedFile.getFileId());

        // Then
        assertFalse(deleted);
        assertTrue(fileRepo.existsById(uploadedFile.getFileId()));
    }

    // ==========================================================================
    // GET CHAT FILES TESTS
    // ==========================================================================

    @Test
    void getChatFiles_ValidInputs_ReturnsFilesList() {
        // Given
        File file1 = fileDbService.uploadFile(user1.getUserId(), chat.getChatId(), testFile);
        MockMultipartFile testFile2 = new MockMultipartFile("file", "test2.txt", "text/plain", "Test content 2".getBytes());
        File file2 = fileDbService.uploadFile(user2.getUserId(), chat.getChatId(), testFile2);

        assertNotNull(file1);
        assertNotNull(file2);

        // When
        List<File> chatFiles = fileDbService.getChatFiles(user1.getUserId(), chat.getChatId());

        // Then
        assertNotNull(chatFiles);
        assertEquals(2, chatFiles.size());

        List<String> fileIds = chatFiles.stream().map(File::getFileId).toList();
        assertTrue(fileIds.contains(file1.getFileId()));
        assertTrue(fileIds.contains(file2.getFileId()));
    }

    @Test
    void getChatFiles_NonExistentUser_ReturnsEmptyList() {
        // When
        List<File> chatFiles = fileDbService.getChatFiles("fake_id", chat.getChatId());

        // Then
        assertNotNull(chatFiles);
        assertTrue(chatFiles.isEmpty());
    }

    @Test
    void getChatFiles_NonExistentChat_ReturnsEmptyList() {
        // When
        List<File> chatFiles = fileDbService.getChatFiles(user1.getUserId(), "fake_chat_id");

        // Then
        assertNotNull(chatFiles);
        assertTrue(chatFiles.isEmpty());
    }

    @Test
    void getChatFiles_UserNotInChat_ReturnsEmptyList() {
        // Given
        User user3 = new User("charlie", "charlie@test.com", "password3");
        entityManager.persistAndFlush(user3);

        // When
        List<File> chatFiles = fileDbService.getChatFiles(user3.getUserId(), chat.getChatId());

        // Then
        assertNotNull(chatFiles);
        assertTrue(chatFiles.isEmpty());
    }

    @Test
    void getChatFiles_EmptyChat_ReturnsEmptyList() {
        // When
        List<File> chatFiles = fileDbService.getChatFiles(user1.getUserId(), chat.getChatId());

        // Then
        assertNotNull(chatFiles);
        assertTrue(chatFiles.isEmpty());
    }
}