package com.MarinGallien.JavaChatApp.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Chat;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Contact;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Message;
import com.MarinGallien.JavaChatApp.Database.JPARepos.ChatRepo;
import com.MarinGallien.JavaChatApp.Database.JPARepos.ContactRepo;
import com.MarinGallien.JavaChatApp.Database.JPARepos.MessageRepo;
import com.MarinGallien.JavaChatApp.Enums.ChatType;
import com.MarinGallien.JavaChatApp.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LocalDatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(LocalDatabaseService.class);

    private final ContactRepo contactRepo;
    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;

    public LocalDatabaseService(ContactRepo contactRepo,
                                ChatRepo chatRepo,
                                MessageRepo messageRepo) {
        this.contactRepo = contactRepo;
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
    }

    // ========== CONTACT SERVICE ==========

    public void syncContacts(List<ContactDTO> serverContacts) {
        try {
            logger.info("Syncing {} contacts to local database", serverContacts.size());

            // Clear existing contacts for current user (simple approach)
            contactRepo.deleteAll();

            // Add all contacts from server
            for (ContactDTO contactDTO : serverContacts) {
                Contact contact = new Contact(
                        contactDTO.getUserId(),
                        contactDTO.getUsername(),
                        contactDTO.getOnlineStatus()
                );
                contactRepo.save(contact);
            }

            logger.info("Successfully synced {} contacts to local database", serverContacts.size());

        } catch (Exception e) {
            logger.error("Error syncing contacts to local database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to sync contacts to local database", e);
        }
    }

    public String findContact(String username) {
        try {
            return contactRepo.findUserIdByUsername(username);
        } catch (Exception e) {
            logger.error("Error retrieving contact: {}", e.getMessage(), e);
            return null;
        }
    }

    public List<Contact> getContacts() {
        try {
            return contactRepo.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving contacts: {}", e.getMessage(), e);
            return null;
        }
    }

    public void updateContactStatus(String userId, com.MarinGallien.JavaChatApp.Enums.OnlineStatus status) {
        try {
            Contact contact = contactRepo.findByUserId(userId);
            if (contact != null) {
                contact.setStatus(status);
                contactRepo.save(contact);
                logger.debug("Updated contact {} status to {}", userId, status);
            }
        } catch (Exception e) {
            logger.error("Error updating contact status: {}", e.getMessage(), e);
        }
    }

    // ========== CHAT SERVICE ==========

    public void syncChats(List<ChatDTO> serverChats) {
        try {
            logger.info("Syncing {} chats to local database", serverChats.size());

            // Get current user's chats and clear them
            String currentUserId = UserSession.getUserId();
            List<Chat> existingChats = chatRepo.findChatsByUserId(currentUserId);
            chatRepo.deleteAll(existingChats);

            // Add all chats from server
            for (ChatDTO chatDTO : serverChats) {
                Chat chat = new Chat(
                chatDTO.getChatId(),
                chatDTO.getChatType(),
                chatDTO.getChatName(),
                chatDTO.getCreatorId(),
                chatDTO.getParticipantIds()
                );

                chatRepo.save(chat);
            }

            logger.info("Successfully synced {} chats to local database", serverChats.size());

        } catch (Exception e) {
            logger.error("Error syncing chats to local database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to sync chats to local database", e);
        }
    }

    public List<Chat> getLocalChats() {
        try {
            String currentUserId = UserSession.getUserId();
            if (currentUserId != null) {
                return chatRepo.findChatsByUserId(currentUserId);
            }
            return List.of();
        } catch (Exception e) {
            logger.error("Error retrieving local chats: {}", e.getMessage(), e);
            return List.of();
        }
    }


    public String findPrivateChat(String contactId) {
        List<Chat> chats = getLocalChats();

        for (Chat chat : chats) {
            if (chat.getChatType() == ChatType.GROUP) {
                continue;
            }
            if (chat.getParticipantIds().contains(contactId)) {
                return chat.getChatId();
            }
        }
        return null;
    }

    public String findGroupChat(String chatName) {
        List<Chat> chats = getLocalChats();

        for (Chat chat : chats) {
            if (chat.getChatType() == ChatType.SINGLE) {
                continue;
            }
            if (chat.getChatName().equals(chatName)) {
                return chat.getChatId();
            }
        }
        return null;
    }


    // ========== MESSAGE SERVICE ==========

    public void syncMessages(String chatId, List<MessageDTO> serverMessages) {
        try {
            logger.info("Syncing {} messages for chat {} to local database", serverMessages.size(), chatId);

            // Clear existing messages for this chat
            messageRepo.deleteByChatId(chatId);

            // Add all messages from server
            for (MessageDTO messageDTO : serverMessages) {
                Message message = new Message(
                messageDTO.getMessageId(),
                messageDTO.getSenderId(),
                messageDTO.getChatId(),
                messageDTO.getContent()
                );

                messageRepo.save(message);
            }

            logger.info("Successfully synced {} messages for chat {} to local database",
                    serverMessages.size(), chatId);

        } catch (Exception e) {
            logger.error("Error syncing messages for chat {} to local database: {}", chatId, e.getMessage(), e);
            throw new RuntimeException("Failed to sync messages to local database", e);
        }
    }

    public void saveMessage(Message message) {
        try {
            messageRepo.save(message);
            logger.debug("Added local message {} to chat {}", message.getMessageId(), message.getChatId());
        } catch (Exception e) {
            logger.error("Error adding local message: {}", e.getMessage(), e);
        }
    }

    public List<Message> getChatMessages(String chatId) {
        try {
            return messageRepo.findByChatChatId(chatId);
        } catch (Exception e) {
            logger.error("Error retrieving chat messages: {}", e.getMessage(), e);
            return null;
        }
    }

}