package com.MarinGallien.JavaChatApp.java_chat_app.EventSystem.Events.FileEvents;

public record DeleteFileRequest(String userId, String chatId, String fileId) implements FileEvents{
}
