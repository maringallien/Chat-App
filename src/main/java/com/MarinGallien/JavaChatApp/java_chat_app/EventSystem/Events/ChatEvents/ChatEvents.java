package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents;

import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications.ChatCreated;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications.ChatDeleted;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications.MemberAddedToChat;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications.MemberRemovedFromChat;
import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Requests.*;

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





