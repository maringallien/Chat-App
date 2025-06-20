package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Notifications;

import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatEvents;

public record ChatDeleted (String chatId) implements ChatEvents {}
