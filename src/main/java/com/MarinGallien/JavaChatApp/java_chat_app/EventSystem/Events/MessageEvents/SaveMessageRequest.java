package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.MessageEvents;

public record SaveMessageRequest (String senderId, String chatId, String content) implements MessageEvents {}
