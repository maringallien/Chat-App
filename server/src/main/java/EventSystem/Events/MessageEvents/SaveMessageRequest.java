package EventSystem.Events.MessageEvents;

public record SaveMessageRequest (String senderId, String chatId, String content) implements MessageEvents {}
