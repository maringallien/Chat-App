package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Requests;

import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatEvents;

public record DeleteChatRequest (String creatorId, String chatId) implements ChatEvents {}
