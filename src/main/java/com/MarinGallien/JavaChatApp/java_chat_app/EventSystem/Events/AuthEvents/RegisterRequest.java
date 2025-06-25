package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.AuthEvents;

public record RegisterRequest (
    String username,
    String email,
    String password
) implements AuthEvents {}
