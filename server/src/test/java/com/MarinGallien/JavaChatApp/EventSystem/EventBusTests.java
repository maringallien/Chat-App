package com.MarinGallien.JavaChatApp.EventSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventBusTests {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private EventBusService eventBusService;

    @BeforeEach
    void setUp() {
        eventBusService = new EventBusService(eventPublisher);
    }

    // Test event class
    static class TestEvent {
        private final String message;
        private final String eventId;
        private final long timestamp;

        public TestEvent(String message) {
            this.message = message;
            this.eventId = java.util.UUID.randomUUID().toString();
            this.timestamp = java.time.Instant.now().toEpochMilli();
        }

        public String getMessage() { return message; }
        public String getEventId() { return eventId; }
        public long getTimestamp() { return timestamp; }
    }

    @Test
    void publishEvent_ValidEvent_CallsEventPublisher() {
        // Given
        TestEvent event = new TestEvent("Hello World");

        // When
        eventBusService.publishEvent(event);

        // Then
        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void publishEvent_NullEvent_LogsAndReturns() {
        // When & Then
        eventBusService.publishEvent(null);

        // Make sure publisher was never called
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void publishEvent_EventPublisherThrowsException_ThrowsRuntimeException() {
        // Given
        TestEvent event = new TestEvent("Test Event");
        doThrow(new RuntimeException("Publisher failed")).when(eventPublisher).publishEvent(event);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventBusService.publishEvent(event);
        });

        assertEquals("Failed to publish event", exception.getMessage());
        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void publishEvent_MultipleEvents_CallsEventPublisherForEach() {
        // Given
        TestEvent event1 = new TestEvent("Event 1");
        TestEvent event2 = new TestEvent("Event 2");
        TestEvent event3 = new TestEvent("Event 3");

        // When
        eventBusService.publishEvent(event1);
        eventBusService.publishEvent(event2);
        eventBusService.publishEvent(event3);

        // Then
        verify(eventPublisher).publishEvent(event1);
        verify(eventPublisher).publishEvent(event2);
        verify(eventPublisher).publishEvent(event3);
    }
}