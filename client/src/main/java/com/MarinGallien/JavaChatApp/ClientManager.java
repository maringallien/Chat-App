package com.MarinGallien.JavaChatApp;

import com.MarinGallien.JavaChatApp.API.APIService;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.LocalDatabaseService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Chat;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Contact;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Message;
import com.MarinGallien.JavaChatApp.WebSocket.ChatService;

import java.util.List;
import java.util.Set;

// ClientManager - handles business logic, not parsing
public class ClientManager {
    private final APIService apiService;
    private final ChatService chatService;
    private final ConsoleUI consoleUI;
    private final LocalDatabaseService localDbService;

    private String userId;
    private String currentChatId;
    private String currentChatPartner;

    public ClientManager(CmdParser cmdParser, APIService apiService, ChatService chatService,
                         ConsoleUI consoleUI, LocalDatabaseService localDbService) {
        this.apiService = apiService;
        this.chatService = chatService;
        this.consoleUI = consoleUI;
        this.localDbService = localDbService;
        userId = UserSession.getInstance().getUserId();
    }

    // Handle user input - delegate parsing
//    public void handleUserInput(String input) {
//        if (consoleUI.isInChatMode()) {
//            handleChatInput(input);
//        } else {
//            // Let CmdParser parse and directly call the appropriate method
//            cmdParser.parseAndExecute(input, this);
//        }
//    }


    // ========== AUTHENTICATION METHODS ==========

    public void handleLogin(String email, String password) {
        try {
            boolean success = apiService.login(email, password);

            if (success) {
                consoleUI.showLoginSuccess(UserSession.getUsername());
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


    // ========== CHAT MANAGEMENT METHODS ==========

    public void enterPrivateChat(String contactUname) {
        try {
            String contactId = localDbService.findContact(contactUname);

            if (contactId == null) {
                consoleUI.showChatNotFound(contactUname);
            }

            String chatId = localDbService.findPrivateChat(contactId);

            if (chatId != null) {
                this.currentChatId = chatId;
                this.currentChatPartner = contactUname;
                consoleUI.enterChatMode(contactUname);

                // Load recent messages for context
                consoleUI.showMessages(localDbService.getChatMessages(chatId));
            } else {
                consoleUI.showChatNotFound(contactUname);
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to enter chat: " + e.getMessage());
        }
    }

    public void enterGroupChat(String chatName) {

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
                consoleUI.showError("Failed to delete chat:");
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
            List<Chat> chats = localDbService.getLocalChats();

            if (chats != null && !chats.isEmpty()) {
                consoleUI.showChats(chats);
            } else {
                consoleUI.showError("Failed to get chats");
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
            List<Contact> contacts = localDbService.getContacts();

            if (contacts != null && !contacts.isEmpty()) {
                consoleUI.showContacts(contacts);
            } else {
                consoleUI.showError("Failed to get contacts");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to get contacts: " + e.getMessage());
        }
    }

    // ========== MESSAGE METHODS ==========

    public void getChatMessages(String chatId) {
        try {
            List<Message> chatMessages = localDbService.getChatMessages(chatId);

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

    // ========== PRIVATE HELPER METHODS ==========

    // Chat input handling (no parsing needed - just send)
    private void handleChatInput(String input) {
        if (input.equals("/exit")) {
            exitCurrentChat();
        } else {
            chatService.sendMessage(currentChatId, input, currentChatPartner);
            consoleUI.showSentMessage(input);
        }
    }

    private void exitCurrentChat() {
        this.currentChatId = null;
        this.currentChatPartner = null;
        consoleUI.exitChatMode();
    }
}
