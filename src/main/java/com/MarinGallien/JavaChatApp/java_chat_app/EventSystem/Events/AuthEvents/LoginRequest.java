package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.AuthEvents;

public record LoginRequest (
    String email,
    String password
) implements AuthEvents{}
