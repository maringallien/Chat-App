package com.MarinGallien.JavaChatApp.Services;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.FileDTO;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.FileDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.File;
import com.MarinGallien.JavaChatApp.Database.Mappers.FileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTests {

    @Mock
    private FileDbService fileDbService;

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private Resource mockResource;

    private File testFile;
    private FileDTO testFileDTO;
    private MultipartFile testMultipartFile;
    private String userId = "user1";
    private String chatId = "chat1";
    private String fileId = "file1";

    @BeforeEach
    void setUp() {
        testFile = new File();

        // Create test DTO
        testFileDTO = new FileDTO(
                fileId,
                "test.txt",
                1024L,
                "text/plain",
                chatId,
                LocalDateTime.now()
        );

        testMultipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );
    }

    // ==========================================================================
    // UPLOAD FILE TESTS
    // ==========================================================================

    @Test
    void uploadFile_ValidInputs_UploadsFile() throws IOException {
        // Given
        when(fileDbService.uploadFile(userId, chatId, testMultipartFile)).thenReturn(testFile);

        // When
        File result = fileService.uploadFile(userId, chatId, testMultipartFile);

        // Then
        assertNotNull(result);
        assertEquals(testFile, result);
        verify(fileDbService).uploadFile(userId, chatId, testMultipartFile);
    }

    @Test
    void uploadFile_InvalidParameters_ReturnsNull() throws IOException {
        // Test all null parameters
        assertNull(fileService.uploadFile(null, chatId, testMultipartFile));
        assertNull(fileService.uploadFile(userId, null, testMultipartFile));
        assertNull(fileService.uploadFile(userId, chatId, null));

        // Test all empty parameters
        assertNull(fileService.uploadFile("", chatId, testMultipartFile));
        assertNull(fileService.uploadFile(userId, "", testMultipartFile));

        // Test all whitespace parameters
        assertNull(fileService.uploadFile("   ", chatId, testMultipartFile));
        assertNull(fileService.uploadFile(userId, "   ", testMultipartFile));

        // Test empty file
        MultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        assertNull(fileService.uploadFile(userId, chatId, emptyFile));

        // Verify no database calls were made
        verify(fileDbService, never()).uploadFile(any(), any(), any());
    }

    @Test
    void uploadFile_DatabaseFailure_ReturnsNull() throws IOException {
        // Given
        when(fileDbService.uploadFile(userId, chatId, testMultipartFile)).thenReturn(null);

        // When
        File result = fileService.uploadFile(userId, chatId, testMultipartFile);

        // Then
        assertNull(result);
        verify(fileDbService).uploadFile(userId, chatId, testMultipartFile);
    }

    // ==========================================================================
    // DOWNLOAD FILE TESTS
    // ==========================================================================

    @Test
    void downloadFile_ValidInputs_ReturnsResource() throws IOException {
        // Given
        when(fileDbService.downloadFile(userId, chatId, fileId)).thenReturn(mockResource);

        // When
        Resource result = fileService.downloadFile(userId, chatId, fileId);

        // Then
        assertNotNull(result);
        assertEquals(mockResource, result);
        verify(fileDbService).downloadFile(userId, chatId, fileId);
    }

    @Test
    void downloadFile_InvalidParameters_ReturnsNull() throws IOException {
        // Test all null parameters
        assertNull(fileService.downloadFile(null, chatId, fileId));
        assertNull(fileService.downloadFile(userId, null, fileId));
        assertNull(fileService.downloadFile(userId, chatId, null));

        // Test all empty parameters
        assertNull(fileService.downloadFile("", chatId, fileId));
        assertNull(fileService.downloadFile(userId, "", fileId));
        assertNull(fileService.downloadFile(userId, chatId, ""));

        // Test all whitespace parameters
        assertNull(fileService.downloadFile("   ", chatId, fileId));
        assertNull(fileService.downloadFile(userId, "   ", fileId));
        assertNull(fileService.downloadFile(userId, chatId, "   "));

        // Verify no database calls were made
        verify(fileDbService, never()).downloadFile(any(), any(), any());
    }

    @Test
    void downloadFile_DatabaseFailure_ReturnsNull() throws IOException {
        // Given
        when(fileDbService.downloadFile(userId, chatId, fileId)).thenReturn(null);

        // When
        Resource result = fileService.downloadFile(userId, chatId, fileId);

        // Then
        assertNull(result);
        verify(fileDbService).downloadFile(userId, chatId, fileId);
    }

    // ==========================================================================
    // DELETE FILE TESTS
    // ==========================================================================

    @Test
    void deleteFile_ValidInputs_DeletesFile() throws IOException {
        // Given
        when(fileDbService.deleteFile(userId, chatId, fileId)).thenReturn(true);

        // When
        boolean result = fileService.deleteFile(userId, chatId, fileId);

        // Then
        assertTrue(result);
        verify(fileDbService).deleteFile(userId, chatId, fileId);
    }

    @Test
    void deleteFile_InvalidParameters_ReturnsFalse() throws IOException {
        // Test all null parameters
        assertFalse(fileService.deleteFile(null, chatId, fileId));
        assertFalse(fileService.deleteFile(userId, null, fileId));
        assertFalse(fileService.deleteFile(userId, chatId, null));

        // Test all empty parameters
        assertFalse(fileService.deleteFile("", chatId, fileId));
        assertFalse(fileService.deleteFile(userId, "", fileId));
        assertFalse(fileService.deleteFile(userId, chatId, ""));

        // Test all whitespace parameters
        assertFalse(fileService.deleteFile("   ", chatId, fileId));
        assertFalse(fileService.deleteFile(userId, "   ", fileId));
        assertFalse(fileService.deleteFile(userId, chatId, "   "));

        // Verify no database calls were made
        verify(fileDbService, never()).deleteFile(any(), any(), any());
    }

    @Test
    void deleteFile_DatabaseFailure_ReturnsFalse() throws IOException {
        // Given
        when(fileDbService.deleteFile(userId, chatId, fileId)).thenReturn(false);

        // When
        boolean result = fileService.deleteFile(userId, chatId, fileId);

        // Then
        assertFalse(result);
        verify(fileDbService).deleteFile(userId, chatId, fileId);
    }

    // ==========================================================================
    // GET CHAT FILES TESTS
    // ==========================================================================

    @Test
    void getChatFiles_ValidInputs_ReturnsFilesList() {
        // Given
        List<File> filesList = List.of(testFile);
        List<FileDTO> fileDTOsList = List.of(testFileDTO);

        when(fileDbService.getChatFiles(userId, chatId)).thenReturn(filesList);
        when(fileMapper.toDTOList(filesList)).thenReturn(fileDTOsList);

        // When
        List<FileDTO> result = fileService.getChatFiles(userId, chatId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFileDTO, result.get(0));

        verify(fileDbService).getChatFiles(userId, chatId);
        verify(fileMapper).toDTOList(filesList);
    }

    @Test
    void getChatFiles_InvalidParameters_ReturnsNull() {
        // Test all null parameters
        assertTrue(fileService.getChatFiles(null, chatId).isEmpty());
        assertTrue(fileService.getChatFiles(userId, null).isEmpty());

        // Test all empty parameters
        assertTrue(fileService.getChatFiles("", chatId).isEmpty());
        assertTrue(fileService.getChatFiles(userId, "").isEmpty());

        // Test all whitespace parameters
        assertTrue(fileService.getChatFiles("   ", chatId).isEmpty());
        assertTrue(fileService.getChatFiles(userId, "   ").isEmpty());

        // Verify no database calls were made
        verify(fileDbService, never()).getChatFiles(any(), any());
    }

    @Test
    void getChatFiles_DatabaseReturnsEmpty_ReturnsEmptyList() {
        // Given
        when(fileDbService.getChatFiles(userId, chatId)).thenReturn(List.of());

        // When
        List<FileDTO> result = fileService.getChatFiles(userId, chatId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(fileDbService).getChatFiles(userId, chatId);
    }

    @Test
    void getChatFiles_DatabaseFailure_ReturnsNull() {
        // Given
        when(fileDbService.getChatFiles(userId, chatId)).thenReturn(List.of());

        // When
        List<FileDTO> result = fileService.getChatFiles(userId, chatId);

        // Then
        assertTrue(result.isEmpty());
        verify(fileDbService).getChatFiles(userId, chatId);
    }
}