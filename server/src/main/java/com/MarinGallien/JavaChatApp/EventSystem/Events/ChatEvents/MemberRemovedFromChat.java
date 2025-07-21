package com.MarinGallien.JavaChatApp.EventSystem.Events.ChatEvents;

public record MemberRemovedFromChat (String userId, String chatId) implements ChatEvents {}
