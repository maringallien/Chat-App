package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.Requests;

import com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.ChatEvents.ChatEvents;

// HTTP Server -> Chat Service (Request Events)
public record CreatePrivateChatRequest (String userId1, String userId2) implements ChatEvents {}
