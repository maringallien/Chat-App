package com.MarinGallien.JavaChatApp.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.Chat;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Message;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.ChatParticipantRepo;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.ChatRepo;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.MessageRepo;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.UserRepo;
import com.MarinGallien.JavaChatApp.Enums.MessageType;
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
    }

    public List<Message> getChatMessages(String senderId, String chatId) {
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
    }
}
