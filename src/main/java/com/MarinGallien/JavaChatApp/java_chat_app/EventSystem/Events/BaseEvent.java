package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEvent {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final String eventType;

    protected BaseEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
    }

    // Getters
    public String getEventId() {return eventId;}
    public LocalDateTime getTimeStamp() {return timestamp;}
    public String getEventType() {return eventType;}

    @Override
    public String toString() {
        return String.format("%s{eventId='%s', timestamp=%s}",
                eventType, eventId, timestamp);
    }
}
