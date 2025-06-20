package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications;

import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatEvents;

import java.util.Set;

// Chat Service -> Chat Manager (Notification Events)
public record ChatCreated (String chatId, Set<String> memberIds) implements ChatEvents {}
