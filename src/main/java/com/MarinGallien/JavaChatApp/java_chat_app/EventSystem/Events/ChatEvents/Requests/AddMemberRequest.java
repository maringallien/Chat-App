package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Requests;

import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatEvents;

public record AddMemberRequest (String creatorId, String userId, String chatId) implements ChatEvents {}
