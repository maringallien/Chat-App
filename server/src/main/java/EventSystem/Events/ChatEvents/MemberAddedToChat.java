package EventSystem.Events.ChatEvents;

public record MemberAddedToChat (String userId, String chatId) implements ChatEvents {}
