package com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents;

// Sealed interface for all chat events
public interface ChatEvents{

    // Every chat event should have a unique identifier
    default String eventId() {
        return java.util.UUID.randomUUID().toString();
    }

    // TimeStamp when the event was created
    default long timestamp() {
        return java.time.Instant.now().toEpochMilli();
    }
}





