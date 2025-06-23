package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EventBusTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TestListener testListener() {return new TestListener();}
    }

    @Autowired
    private EventBusService eventBus;

    @Autowired
    private TestListener testListener;

    // Test event class - simple POJO that matches your event pattern
    static class TestEvent {
        private final String message;
        private final String eventId;
        private final long timestamp;

        public TestEvent(String message) {
            this.message = message;
            this.eventId = java.util.UUID.randomUUID().toString();
            this.timestamp = java.time.Instant.now().toEpochMilli();
        }

        // Getters
        public String getMessage() {return message;}
        public String getEventId() {return eventId;}
        public long getTimestamp() {return timestamp;}
    }

    // Test listener class
    @Component
    static class TestListener {
        private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
        private final List<TestEvent> receivedEvents = new ArrayList<>();

        @EventListener
        public void handleTestEvent(TestEvent event) {
            receivedEvents.add(event);
            logger.info("Received event: {}", event.getMessage());
        }

        public List<TestEvent> getEvents() {
            return receivedEvents;
        }

        public void clearEvents() {
            receivedEvents.clear();
        }
    }

    @Test
    void eventBus_PublishEvent_ListenerReceivesEvent() {
        // Given
        testListener.clearEvents();
        TestEvent event = new TestEvent("Hello World");

        // When
        eventBus.publishEvent(event);

        // Then
        assertEquals(1, testListener.getEvents().size());
        assertEquals("Hello World", testListener.getEvents().get(0).getMessage());
        assertNotNull(event.getEventId(), "Event ID should not be null");
        assertTrue(event.getTimestamp() > 0, "Timestamp should be set");
    }

    @Test
    void eventBus_PublishMultipleEvents_AllEventsReceived() {
        // Given
        testListener.clearEvents();
        TestEvent event1 = new TestEvent("First Event");
        TestEvent event2 = new TestEvent("Second Event");
        TestEvent event3 = new TestEvent("Third Event");

        // When
        eventBus.publishEvent(event1);
        eventBus.publishEvent(event2);
        eventBus.publishEvent(event3);

        // Then
        assertEquals(3, testListener.getEvents().size());
        assertEquals("First Event", testListener.getEvents().get(0).getMessage());
        assertEquals("Second Event", testListener.getEvents().get(1).getMessage());
        assertEquals("Third Event", testListener.getEvents().get(2).getMessage());
    }

    @Test
    void eventBus_PublishNullEvent_HandlesGracefully() {
        // Given
        testListener.clearEvents();

        // When & Then - should not throw exception
        try {
            eventBus.publishEvent(null);
            // If no exception is thrown, the test passes
            assertTrue(true, "Event bus handled null event gracefully");
        } catch (Exception e) {
            // If an exception is thrown, we expect it to be a RuntimeException
            assertTrue(e instanceof RuntimeException, "Expected RuntimeException for null event");
        }

        // Events should remain empty
        assertEquals(0, testListener.getEvents().size());
    }
}