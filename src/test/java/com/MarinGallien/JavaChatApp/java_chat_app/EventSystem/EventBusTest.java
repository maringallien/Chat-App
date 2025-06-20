//package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem;
//
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.test.util.AssertionErrors.assertNotNull;
//
//@SpringBootTest
//public class EventBusTest {
//
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public TestListener testListener() {
//            return new TestListener();
//        }
//    }
//    @Autowired
//    private EventBusService eventBus;
//    @Autowired
//    private TestListener testListener;
//
//
//    // Test event class
//    static class TestEvent extends BaseEvent {
//        private final String message;
//
//        // Constructor
//        public TestEvent(String message) {
//            super("TEST_EVENT");
//            this.message = message;
//        }
//
//        // Getter
//        private String getMessage() { return message;}
//    }
//
//
//    // Test listener class
//    @Component
//    static class TestListener {
//        private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
//        private final List<TestEvent> receivedEvents = new ArrayList<>();
//
//        @EventListener
//        public void handleTestEvent(TestEvent event) {
//            receivedEvents.add(event);
//            logger.info("Received event: {}", event.getMessage());
//        }
//
//        // Getter
//        public List<TestEvent> getEvents() {
//            return receivedEvents;
//        }
//
//        // Helper method
//        public void clearEvents() {
//            receivedEvents.clear();
//        }
//    }
//
//
//    // Test to make sure events can be properly published to and retrieved from the event bus\
//    @Test
//    void eventBus_PublishEvent_ListenerReceivesEvent() {
//        // Given
//        testListener.clearEvents();
//        TestEvent event = new TestEvent("Hello World");
//
//        // When
//        eventBus.publishEvent(event);
//
//        // Then
//        assertEquals(1, testListener.getEvents().size());
//        assertEquals("Hello World", testListener.getEvents().get(0).getMessage());
//        assertNotNull(event.getEventId(), "Event ID should not be null");
//        assertEquals("TEST_EVENT", event.getEventType());
//    }
//}
