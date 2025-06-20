package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Requests;

import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatEvents;

import java.util.Set;

public record CreateGroupChatRequest (String creatorId, Set<String> memberIds, String chatName) implements ChatEvents {}
