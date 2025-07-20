package Database.DatabaseServices;

import Database.JPAEntities.CoreEntities.Chat;
import Database.JPAEntities.CoreEntities.Message;
import Database.JPAEntities.CoreEntities.User;
import Database.JPARepositories.ChatParticipantRepo;
import Database.JPARepositories.ChatRepo;
import Database.JPARepositories.MessageRepo;
import Database.JPARepositories.UserRepo;
import Enums.MessageType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MessageDbService {

    private static Logger logger = LoggerFactory.getLogger(MessageDbService.class);

    private final MessageRepo messageRepo;
    private final ChatParticipantRepo chatParticipantRepo;
    private final UserRepo userRepo;
    private final ChatRepo chatRepo;

    public MessageDbService(MessageRepo messageRepo, ChatParticipantRepo chatParticipantRepo, UserRepo userRepo,
                            ChatRepo chatRepo) {
        this.messageRepo = messageRepo;
        this.chatParticipantRepo = chatParticipantRepo;
        this.userRepo = userRepo;
        this.chatRepo = chatRepo;
    }

    public Message saveMessage (String senderId, String chatId, String content) {
        try {
            // Perform validation
            if (!userRepo.existsById(senderId)) {
                logger.warn("Failed to save message: user {} does not exist", senderId);
                return null;
            }

            if (!chatRepo.existsById(chatId)) {
                logger.warn("Failed to save message: chat {} does not exist", chatId);
                return null;
            }

            if (!chatParticipantRepo.existsByChatChatIdAndUserUserId(chatId, senderId)){
                logger.warn("Failed to save message: user {} is not a participant of chat {}", senderId, chatId);
                return null;
            }
            // Extract sender and chat
            User sender = userRepo.findUserById(senderId);
            Chat chat = chatRepo.findChatById(chatId);

            // Save message
            Message message = new Message(sender, chat, content, MessageType.TEXT_MESSAGE);

            return messageRepo.save(message);

        } catch (Exception e) {
            logger.error("Failed to save message: {}", e.getMessage());
            return null;
        }
    }

    public List<Message> getChatMessages(String senderId, String chatId) {
        try {
            // Validate input
            if (!userRepo.existsById(senderId)) {
                logger.warn("Failed to retrieve messages: sender {} does not exist", senderId);
                return List.of();
            }

            if (!chatRepo.existsById(chatId)) {
                logger.warn("Failed to retrieve messages: chat {} does not exist", chatId);
                return List.of();
            }

            if (!chatParticipantRepo.existsByChatChatIdAndUserUserId(chatId, senderId)){
                logger.warn("Failed to retrieve messages: user {} is not a participant of chat {}", senderId, chatId);
                return List.of();
            }

            List<Message> messages = messageRepo.findByChatChatIdOrderBySentAtAsc(chatId);

            return messages!= null ? messages : List.of();

        } catch (Exception e) {
            logger.error("Failed to retrieve messages: {}", e.getMessage());
            return List.of();
        }
    }
}
