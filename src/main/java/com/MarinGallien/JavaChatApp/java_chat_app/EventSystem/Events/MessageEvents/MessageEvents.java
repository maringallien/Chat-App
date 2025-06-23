package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.MessageEvents;

public interface MessageEvents {

    // Every message event should have a unique identifier
    default String eventId() {
        return java.util.UUID.randomUUID().toString();
    }

    // TimeStamp when the event was created
    default long timestamp() {
        return java.time.Instant.now().toEpochMilli();
    }
}
