package com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents;

import java.util.Set;

// Chat Service -> Chat Manager (Notification Events)
public record ChatCreated (String chatId, Set<String> memberIds) implements ChatEvents {}
