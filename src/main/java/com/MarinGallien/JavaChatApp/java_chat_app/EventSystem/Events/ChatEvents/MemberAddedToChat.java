package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents;

public record MemberAddedToChat (String userId, String chatId) implements ChatEvents {}
