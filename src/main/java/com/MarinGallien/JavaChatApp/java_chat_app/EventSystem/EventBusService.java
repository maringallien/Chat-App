package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem;

import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EventBusService {
    private static final Logger logger = LoggerFactory.getLogger(EventBusService.class);
    private final ApplicationEventPublisher eventPublisher;

    public EventBusService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent(BaseEvent event) {
        try {
            logger.info("Publishing event: {} with ID: {}", event.getEventType(), event.getEventId());
            eventPublisher.publishEvent(event);
            logger.info("Successfully published event: {} with ID: {}", event.getEventType(), event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish even: {} with ID: {}", event.getEventType(), event.getEventId());
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
