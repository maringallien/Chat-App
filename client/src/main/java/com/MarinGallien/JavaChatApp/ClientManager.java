package com.MarinGallien.JavaChatApp;

import com.MarinGallien.JavaChatApp.API.APIService;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.ChatDbService;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.ContactDbService;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.MessageDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Chat;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Contact;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Message;
import com.MarinGallien.JavaChatApp.WebSocket.ChatService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// ClientManager - handles business logic, not parsing
public class ClientManager {

    // Network services
    private final APIService apiService;
    private final ChatService chatService;

    // Database services
    private final ChatDbService chatDbService;
    private final ContactDbService contactDbService;
    private final MessageDbService messageDbService;

    // UI service
    private final ConsoleUI consoleUI;

    // Local parameters
    private String userId;
    private String currentChatId;

    public ClientManager(APIService apiService, ChatService chatService, ChatDbService chatDbService,
                         ContactDbService contactDbService, MessageDbService messageDbService, ConsoleUI consoleUI) {
        this.apiService = apiService;
        this.chatService = chatService;
        this.chatDbService = chatDbService;
        this.contactDbService = contactDbService;
        this.messageDbService = messageDbService;
        this.consoleUI = consoleUI;
        userId = UserSession.getInstance().getUserId();
    }


    // ========== AUTHENTICATION METHODS ==========

    public void handleLogin(String email, String password) {
        try {
            boolean success = apiService.login(email, password);

            if (success) {
                consoleUI.showLoginSuccess(UserSession.getUsername());
                chatService.startChat();
            } else {
                consoleUI.showLoginFailure();
            }
        } catch (Exception e) {
            consoleUI.showError("Login failed: " + e.getMessage());
        }
    }

    public void handleRegister(String username, String email, String password) {
        try {
            boolean success = apiService.register(username, email, password);

            if (success) {
                consoleUI.showRegistrationSuccess();
            } else {
                consoleUI.showRegistrationFailure();
            }
        } catch (Exception e) {
            consoleUI.showError("Registration failed: " + e.getMessage());
        }
    }


    // ========== CHAT MODE UTILITIES FOR CMDPARSER ==========

    public boolean isInChatMode() {
        return consoleUI.isInChatMode();
    }

    public void sendMessage(String message) {
        if (currentChatId == null) {
            consoleUI.showError("Not in a chat. Use 'chat <contact>' to start chatting.");
            return;
        }

        if (!chatService.isConnected()) {
            consoleUI.showError("Not connected to chat server.");
            return;
        }

        try {
            chatService.sendMessage(currentChatId, message);
            consoleUI.showSentMessage(message);
        } catch (Exception e) {
            consoleUI.showError("Failed to send message: " + e.getMessage());

        }
    }

    public void exitCurrentChat() {
        if (currentChatId != null) {
            this.currentChatId = null;
            consoleUI.exitChatMode();
        }
    }

    public void disconnect() {
        try {
            // Stop chat service (sends offline status and disconnects WebSocket)
            if (chatService != null && chatService.isConnected()) {
                chatService.stopChat();
            }

            // Clear JWT token (logout from API)
            if (apiService != null) {
                apiService.logout();
            }

            // Exit any current chat
            if (currentChatId != null) {
                exitCurrentChat();
            }


        } catch (Exception e) {
            consoleUI.showError("Error during disconnect: " + e.getMessage());
        }
    }



    // ========== CHAT MANAGEMENT METHODS ==========


    public void enterPrivateChat(String contactUname) {
        try {
            String contactId = contactDbService.findContact(contactUname);

            if (contactId == null) {
                consoleUI.showChatNotFound(contactUname);
            }

            String chatId = chatDbService.findPrivateChat(contactId);

            if (chatId != null) {
                this.currentChatId = chatId;
                consoleUI.enterChatMode(contactUname);

                // Load recent messages for context
                consoleUI.showMessages(messageDbService.getChatMessages(chatId));
            } else {
                consoleUI.showChatNotFound(contactUname);
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to enter chat: " + e.getMessage());
        }
    }

    public void enterGroupChat(String chatName) {
        try {
            String chatId = chatDbService.findGroupChat(chatName);

            if (chatId != null) {
                this.currentChatId = chatId;
                consoleUI.enterChatMode(chatName);

                consoleUI.showMessages(messageDbService.getChatMessages(chatId));
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to enter chat: " + e.getMessage());
        }
    }

    public void createPrivateChat(String userId2) {
        try {
            boolean success = apiService.createPrivateChat(userId2);

            if (success) {
                consoleUI.showPrivateChatCreated(userId2);
            } else {
                consoleUI.showError("Failed to create private chat");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to create private chat: " + e.getMessage());
        }
    }

    public void createGroupChat(String chatName, String[] memberIds) {
        try {
            Set<String> memberSet = Set.of(memberIds);
            boolean success = apiService.createGroupChat(memberSet, chatName);

            if (success) {
                consoleUI.showGroupChatCreated(chatName);
            } else {
                consoleUI.showError("Failed to create group chat");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to create group chat: " + e.getMessage());
        }
    }

    public void deleteChat(String chatId) {
        try {
            boolean success = apiService.deleteChat(chatId);

            if (success) {
                consoleUI.showSuccess("Chat deleted successfully");

                // Exit chat mode if we're currently in this chat
                if (chatId.equals(currentChatId)) {
                    exitCurrentChat();
                }
            } else {
                consoleUI.showError("Failed to delete chat");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to delete chat: " + e.getMessage());
        }
    }

    public void addMemberToChat(String chatId, String userId) {
        try {
            boolean success = apiService.addMemberToChat(userId, chatId);

            if (success) {
                consoleUI.showSuccess("Member added to chat successfully");
            } else {
                consoleUI.showError("Failed to add member");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to add member: " + e.getMessage());
        }
    }

    public void removeMemberFromChat(String chatId, String memberId) {
        try {
            boolean success = apiService.removeMemberFromChat(memberId, chatId);

            if (success) {
                consoleUI.showSuccess("Member removed from chat successfully");
            } else {
                consoleUI.showError("Failed to remove member");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to remove member: " + e.getMessage());
        }
    }

    public void getUserChats() {
        try {
            // Sync chats and retrieve updated list of chats
            syncChats();
            List<Chat> chats = chatDbService.getLocalChats();

            // Check for null or empty
            if (chats == null || chats.isEmpty()) {
                consoleUI.showError("No chats were found");
                return;
            }

            // Display chats
            consoleUI.showChats(chats);
        } catch (Exception e) {
            consoleUI.showError("Failed to get chats: " + e.getMessage());
        }
    }

    public void syncChats() {
        try {
            // Retrieve all chats from server
            List<ChatDTO> chats = apiService.getUserChats();
            if (chats == null) {
                return;
            }

            // Identify chats created after latest chat stored locally
            LocalDateTime lastChatTimestamp = chatDbService.getLastChatTimestamp();
            List<ChatDTO> newChats = chats.stream()
                    .filter(chat -> lastChatTimestamp == null || chat.getCreatedAt().isAfter(lastChatTimestamp))
                    .toList();

            // Add new chats to database
            if (!newChats.isEmpty()) {
                chatDbService.addNewChats(newChats);
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to get chats: " + e.getMessage());
        }
    }


    // ========== CONTACT MANAGEMENT METHODS ==========

    public void addContact(String contactId) {
        try {
            boolean success = apiService.createContact(contactId);

            if (success) {
                consoleUI.showContactAdded(contactId);
            } else {
                consoleUI.showError("Failed to add contact");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to add contact: " + e.getMessage());
        }
    }

    public void removeContact(String contactId) {
        try {
            boolean success = apiService.removeContact(contactId);

            if (success) {
                consoleUI.showContactRemoved(contactId);
            } else {
                consoleUI.showError("Failed to remove contact");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to remove contact: " + e.getMessage());
        }
    }

    public void getUserContacts() {
        try {
            syncContacts();
            List<Contact> contacts = contactDbService.getContacts();

            if (contacts != null && !contacts.isEmpty()) {
                consoleUI.showContacts(contacts);
            } else {
                consoleUI.showError("No contacts found");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to get contacts: " + e.getMessage());
        }
    }

    public void syncContacts() {
        try {
            // Retrieve all contacts from server
            List<ContactDTO> contacts = apiService.getUserContacts();
            if (contacts == null) {
                return;
            }

            // Identify contacts created after latest chat stored locally
            LocalDateTime lastTimestamp = chatDbService.getLastChatTimestamp();
            List<ContactDTO> newContacts = contacts.stream()
                    .filter(contact -> lastTimestamp == null || contact.getCreatedAt().isAfter(lastTimestamp))
                    .toList();

            // Add new contacts to database
            if (!newContacts.isEmpty()) {
                contactDbService.addNewContacts(newContacts);
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to enter chat: " + e.getMessage());
        }
    }


    // ========== MESSAGE METHODS ==========

    public void getChatMessages(String chatId) {
        try {
            List<Message> chatMessages = messageDbService.getChatMessages(chatId);

            if (chatMessages != null && !chatMessages.isEmpty()) {
                consoleUI.showMessages(chatMessages);
            } else {
                consoleUI.showError("Failed to get messages");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to get messages: " + e.getMessage());
        }
    }


    // ========== USER UPDATE METHODS ==========

    public void updateUsername(String newUsername) {
        try {
            boolean success = apiService.updateUsername(newUsername);

            if (success) {
                consoleUI.showSuccess("Username updated successfully");
            } else {
                consoleUI.showError("Failed to update username");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to update username: " + e.getMessage());
        }
    }

    public void updateEmail(String newEmail) {
        try {
            boolean success = apiService.updateEmail(newEmail);

            if (success) {
                consoleUI.showSuccess("Email updated successfully");
            } else {
                consoleUI.showError("Failed to update email");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to update email: " + e.getMessage());
        }
    }

    public void updatePassword(String oldPassword, String newPassword) {
        try {
            boolean success = apiService.updatePassword(oldPassword, newPassword);

            if (success) {
                consoleUI.showSuccess("Password updated successfully");
            } else {
                consoleUI.showError("Failed to update password");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to update password: " + e.getMessage());
        }
    }


    // ========== UTILITY METHODS ==========

    public void showHelp() {
        consoleUI.showHelp();
    }


}
