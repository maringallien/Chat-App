package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import DTOs.WebsocketMessages.WebSocketMessage;
import Services.OfflineMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OfflineMessageServiceTests {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ListOperations<String, String> listOperations;

    @InjectMocks
    private OfflineMessageService offlineMessageService;

    private WebSocketMessage testMessage;
    private String userId = "user1";
    private String messageJson = "{\"messageID\":\"msg1\",\"type\":\"TEXT_MESSAGE\",\"content\":\"Hello\"}";
    private String redisKey = "offline_messageuser1";

    @BeforeEach
    void setUp() {
        testMessage = new WebSocketMessage("sender1", "chat1", "Hello World!", "recipient1");
    }

    // ==========================================================================
    // STORE OFFLINE MESSAGE TESTS
    // ==========================================================================

    @Test
    void storeOfflineMessage_ValidInputs_StoresMessage() throws JsonProcessingException {
        // Given
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(5L); // Mock redis queue size
        when(objectMapper.writeValueAsString(testMessage)).thenReturn(messageJson); // Mock message serialization

        // When
        boolean result = offlineMessageService.storeOfflineMessage(userId, testMessage);

        // Then
        assertTrue(result);
        verify(listOperations).size(redisKey);
        verify(objectMapper).writeValueAsString(testMessage);
        verify(listOperations).rightPush(redisKey, messageJson);
        verify(redisTemplate).expire(redisKey, Duration.ofDays(7));
    }

    @Test
    void storeOfflineMessage_InvalidParameters_ReturnsFalse() {
        // Test null user ID
        assertFalse(offlineMessageService.storeOfflineMessage(null, testMessage));
        assertFalse(offlineMessageService.storeOfflineMessage(userId, null));

        // Test empty user ID
        assertFalse(offlineMessageService.storeOfflineMessage("", testMessage));

        // Test whitespace user ID
        assertFalse(offlineMessageService.storeOfflineMessage("   ", testMessage));

        // Verify no Redis operations were performed
        verifyNoInteractions(redisTemplate);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void storeOfflineMessage_QueueFull_ReturnsFalse() throws JsonProcessingException {
        // Given - queue is at maximum capacity
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(1000L);

        // When
        boolean result = offlineMessageService.storeOfflineMessage(userId, testMessage);

        // Then
        assertFalse(result);
        verify(listOperations).size(redisKey);
        verify(objectMapper, never()).writeValueAsString(testMessage);
        verify(listOperations, never()).rightPush(any(), any());
    }

    @Test
    void storeOfflineMessage_SerializationError_ReturnsFalse() throws JsonProcessingException {
        // Given
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(5L);
        when(objectMapper.writeValueAsString(testMessage))
                .thenThrow(new JsonProcessingException("Serialization error") {});

        // When
        boolean result = offlineMessageService.storeOfflineMessage(userId, testMessage);

        // Then
        assertFalse(result);
        verify(listOperations).size(redisKey);
        verify(objectMapper).writeValueAsString(testMessage);
        verify(listOperations, never()).rightPush(any(), any());
    }

    @Test
    void storeOfflineMessage_RedisError_ReturnsFalse() {
        // Given
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenThrow(new RuntimeException("Redis connection error"));

        // When
        boolean result = offlineMessageService.storeOfflineMessage(userId, testMessage);

        // Then
        assertFalse(result);
        verify(listOperations).size(redisKey);
        verifyNoMoreInteractions(objectMapper);
    }

    // ==========================================================================
    // RETRIEVE PENDING MESSAGES TESTS
    // ==========================================================================

    @Test
    void retrievePendingMessages_ValidInputs_ReturnsMessages() throws JsonProcessingException {
        // Given
        List<String> batch = List.of(messageJson);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(1L);
        when(listOperations.range(redisKey, 0, 49))
                .thenReturn(batch)
                .thenReturn(List.of());
        when(objectMapper.readValue(messageJson, WebSocketMessage.class)).thenReturn(testMessage);
        when(listOperations.leftPop(redisKey)).thenReturn(messageJson);

        // When
        List<WebSocketMessage> result = offlineMessageService.retrievePendingMessages(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMessage, result.get(0));
        verify(listOperations).size(redisKey);
        verify(listOperations, times(2)).range(redisKey, 0, 49);
        verify(objectMapper).readValue(messageJson, WebSocketMessage.class);
        verify(listOperations).leftPop(redisKey);
    }

    @Test
    void retrievePendingMessages_InvalidParameters_ReturnsEmptyList() {
        // Test null user ID
        List<WebSocketMessage> result1 = offlineMessageService.retrievePendingMessages(null);
        List<WebSocketMessage> result2 = offlineMessageService.retrievePendingMessages("");
        List<WebSocketMessage> result3 = offlineMessageService.retrievePendingMessages("   ");

        // Should return empty list for invalid IDs
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());

        // Verify no Redis operations were performed
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void retrievePendingMessages_NoMessages_ReturnsEmptyList() {
        // Given
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(0L);

        // When
        List<WebSocketMessage> result = offlineMessageService.retrievePendingMessages(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(listOperations).size(redisKey);
        verify(listOperations, never()).range(any(), anyLong(), anyLong());
    }

    @Test
    void retrievePendingMessages_DeserializationError_SkipsMessage() throws JsonProcessingException {
        // Given
        List<String> batch = List.of(messageJson);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(1L);
        when(listOperations.range(redisKey, 0, 49))
                .thenReturn(batch)
                .thenReturn(List.of());
        when(objectMapper.readValue(messageJson, WebSocketMessage.class))
                .thenThrow(new JsonProcessingException("Deserialization error") {}); // Throw error here
        when(listOperations.leftPop(redisKey)).thenReturn(messageJson);

        // When
        List<WebSocketMessage> result = offlineMessageService.retrievePendingMessages(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Message was skipped due to deserialization error
        verify(listOperations).size(redisKey);
        verify(listOperations, times(2)).range(redisKey, 0, 49);
        verify(objectMapper).readValue(messageJson, WebSocketMessage.class);
        verify(listOperations).leftPop(redisKey); // Still removes the bad message
    }

    @Test
    void retrievePendingMessages_RedisError_ReturnsEmptyList() {
        // Given
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenThrow(new RuntimeException("Redis connection error"));

        // When
        List<WebSocketMessage> result = offlineMessageService.retrievePendingMessages(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(listOperations).size(redisKey);
        verifyNoMoreInteractions(listOperations);
    }

    // ==========================================================================
    // HAS PENDING MESSAGES TESTS
    // ==========================================================================

    @Test
    void hasPendingMessages_ValidInputWithMessages_ReturnsTrue() {
        // Given
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(5L);

        // When
        boolean result = offlineMessageService.hasPendingMessages(userId);

        // Then
        assertTrue(result);
        verify(listOperations).size(redisKey);
    }

    @Test
    void hasPendingMessages_ValidInputNoMessages_ReturnsFalse() {
        // Given
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(0L);

        // When
        boolean result = offlineMessageService.hasPendingMessages(userId);

        // Then
        assertFalse(result);
        verify(listOperations).size(redisKey);
    }

    @Test
    void hasPendingMessages_InvalidParameters_ReturnsFalse() {
        // Test null user ID
        assertFalse(offlineMessageService.hasPendingMessages(null));

        // Test empty user ID
        assertFalse(offlineMessageService.hasPendingMessages(""));

        // Test whitespace user ID
        assertFalse(offlineMessageService.hasPendingMessages("   "));

        // Verify no Redis operations were performed
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void hasPendingMessages_RedisReturnsNull_ReturnsFalse() {
        // Given
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(null);

        // When
        boolean result = offlineMessageService.hasPendingMessages(userId);

        // Then
        assertFalse(result);
        verify(listOperations).size(redisKey);
    }

    @Test
    void hasPendingMessages_RedisError_ReturnsFalse() {
        // Given
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenThrow(new RuntimeException("Redis connection error"));

        // When
        boolean result = offlineMessageService.hasPendingMessages(userId);

        // Then
        assertFalse(result);
        verify(listOperations).size(redisKey);
    }

    // ==========================================================================
    // EDGE CASE AND INTEGRATION TESTS
    // ==========================================================================

    @Test
    void storeOfflineMessage_UserIdWithTrim_HandlesCorrectly() throws JsonProcessingException {
        // Given
        String userIdWithSpaces = "  user1  ";
        String expectedKey = "offline_messageuser1"; // Should be trimmed
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(expectedKey)).thenReturn(5L);
        when(objectMapper.writeValueAsString(testMessage)).thenReturn(messageJson);

        // When
        boolean result = offlineMessageService.storeOfflineMessage(userIdWithSpaces, testMessage);

        // Then
        assertTrue(result);
        verify(listOperations).size(expectedKey);
        verify(listOperations).rightPush(expectedKey, messageJson);
    }

    @Test
    void retrievePendingMessages_MultipleMessages_ReturnsAllMessages() throws JsonProcessingException {
        // Given
        WebSocketMessage message2 = new WebSocketMessage("sender2", "chat2", "Hello Again!", "recipient2");
        String messageJson2 = "{\"messageID\":\"msg2\",\"type\":\"TEXT_MESSAGE\",\"content\":\"Hello Again\"}";
        List<String> batch = List.of(messageJson, messageJson2);

        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(2L);
        when(listOperations.range(redisKey, 0, 49))
                .thenReturn(batch)
                .thenReturn(List.of());
        when(objectMapper.readValue(messageJson, WebSocketMessage.class)).thenReturn(testMessage);
        when(objectMapper.readValue(messageJson2, WebSocketMessage.class)).thenReturn(message2);
        when(listOperations.leftPop(redisKey)).thenReturn(messageJson, messageJson2);

        // When
        List<WebSocketMessage> result = offlineMessageService.retrievePendingMessages(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testMessage));
        assertTrue(result.contains(message2));
        verify(listOperations, times(2)).leftPop(redisKey);
    }

    @Test
    void storeOfflineMessage_AtMaxCapacity_ReturnsFalse() {
        // Given - exactly at max capacity
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.size(redisKey)).thenReturn(1000L);

        // When
        boolean result = offlineMessageService.storeOfflineMessage(userId, testMessage);

        // Then
        assertFalse(result);
        verify(listOperations).size(redisKey);
        verifyNoMoreInteractions(objectMapper);
    }
}