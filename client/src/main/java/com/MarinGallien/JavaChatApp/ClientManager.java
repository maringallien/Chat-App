package com.MarinGallien.JavaChatApp;

import com.MarinGallien.JavaChatApp.API.APIService;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.WebSocket.ChatService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// ClientManager - handles business logic, not parsing
public class ClientManager {

    // Network services
    private final APIService apiService;
    private final ChatService chatService;

    // UI service
    private final ConsoleUI consoleUI;

    // Local parameters
    private String userId;
    private String currentChatId;

    public ClientManager(APIService apiService, ChatService chatService, ConsoleUI consoleUI) {
        this.apiService = apiService;
        this.chatService = chatService;
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

    // METHOD NEEDS WORK. NEED TO FIND CHAT ID FROM USERNAME OR USER ID
    public void enterPrivateChat(String contactUname) {
        try {
            // Retrieve user ID
            String contactUserId = apiService.getUserIdFromUsername(contactUname);

            if (contactUserId == null || contactUserId.isEmpty()) {
                consoleUI.showError("Failed to retrieve contact user ID");
                return;
            }

            // Generate private chat ID (they are predictable)
            String chatId = determineChatId(userId, contactUserId);

            if (chatId != null) {
                this.currentChatId = chatId;
                consoleUI.enterChatMode(contactUname);

                List<MessageDTO> messages = apiService.getChatMessages(chatId);

                if (!messages.isEmpty()) {
                    consoleUI.showMessages(messages);
                }
            } else {
                consoleUI.showChatNotFound(contactUname);
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to enter chat: " + e.getMessage());
        }
    }

    private String determineChatId(String userId1, String userId2) {
        String[] sortedIds = {userId1, userId2};
        Arrays.sort(sortedIds);
        return "PRIVATE_" + sortedIds[0] + "_" + sortedIds[1];
    }


    public void enterGroupChat(String chatName) {
        try {
            // Retrieve chat ID from chat name
            String chatId = apiService.getChatIdFromChatName(chatName);
            if (chatId == null || chatId.isEmpty()) {
                consoleUI.showError("Failed to retrieve chat ID");
                return;
            }

            this.currentChatId = chatId;
            consoleUI.enterChatMode(chatName);
            consoleUI.showMessages(apiService.getChatMessages(chatId));

        } catch (Exception e) {
            consoleUI.showError("Failed to enter chat: " + e.getMessage());
        }
    }

    public void createPrivateChat(String contactUname) {
        try {
            // Retrieve contact ID from contact username
            String contactId = apiService.getUserIdFromUsername(contactUname);
            if (contactId == null || contactId.isEmpty()) {
                consoleUI.showError("Failed to retrieve user ID");
                return;
            }

            boolean success = apiService.createPrivateChat(contactId);

            if (success) {
                consoleUI.showPrivateChatCreated(contactId);
            } else {
                consoleUI.showError("Failed to create private chat");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to create private chat: " + e.getMessage());
        }
    }

    public void createGroupChat(String chatName, List<String> memberUnames) {
        try {
            // Retrieve member IDs from member usernames
            List<String> memberIds = apiService.getUserIdsFromUsernames(memberUnames);
            if (memberIds == null || memberIds.isEmpty()) {
                consoleUI.showError("Failed to retrieve user IDs");
                return;
            }

            Set<String> memberSet = new HashSet<>(memberIds);
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

    public void deleteChat(String chatName) {
        try {
            // Retrieve chat ID from chat name
            String chatId = apiService.getChatIdFromChatName(chatName);
            if (chatId == null || chatId.isEmpty()) {
                consoleUI.showError("Failed to retrieve chat ID");
                return;
            }

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

    public void addMemberToChat(String chatName, String username) {
        try {
            // Retrieve chat and member IDs from chat name and username
            String chatId = apiService.getChatIdFromChatName(chatName);
            String memberId = apiService.getUserIdFromUsername(username);
            if (memberId == null || chatId == null || memberId.isEmpty() || chatId.isEmpty()) {
                consoleUI.showError("Failed to retrieve chat and/or user ID");
                return;
            }

            boolean success = apiService.addMemberToChat(memberId, chatId);

            if (success) {
                consoleUI.showSuccess("Member added to chat successfully");
            } else {
                consoleUI.showError("Failed to add member");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to add member: " + e.getMessage());
        }
    }

    public void removeMemberFromChat(String chatName, String username) {
        try {
            // Retrieve chat and member IDs from chat name and username
            String chatId = apiService.getChatIdFromChatName(chatName);
            String memberId = apiService.getUserIdFromUsername(username);
            if (memberId == null || chatId == null || memberId.isEmpty() || chatId.isEmpty()) {
                consoleUI.showError("Failed to retrieve chat and/or user ID");
                return;
            }

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
            List<ChatDTO> chats = apiService.getUserChats();

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

    // ========== CONTACT MANAGEMENT METHODS ==========

    public void addContact(String contactUname) {
        try {
            // Retrieve contact ID from contact username
            String contactId = apiService.getUserIdFromUsername(contactUname);
            if (contactId == null || contactId.isEmpty()) {
                consoleUI.showError("Failed to retrieve contact ID");
            }

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

    public void removeContact(String contactUname) {
        try {
            // Retrieve contact ID from contact username
            String contactId = apiService.getUserIdFromUsername(contactUname);
            if (contactId == null || contactId.isEmpty()) {
                consoleUI.showError("Failed to retrieve contact ID");
            }

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
            List<ContactDTO> contacts = apiService.getUserContacts();

            if (contacts != null && !contacts.isEmpty()) {
                consoleUI.showContacts(contacts);
            } else {
                consoleUI.showError("No contacts found");
            }
        } catch (Exception e) {
            consoleUI.showError("Failed to get contacts: " + e.getMessage());
        }
    }


    // ========== MESSAGE METHODS ==========

    public void getChatMessages(String chatName) {
        try {
            // Retrieve chat ID from chat name
            String chatId = apiService.getChatIdFromChatName(chatName);
            if (chatName == null || chatName.isEmpty()) {
                consoleUI.showError("Failed to retrieve chat ID");
                return;
            }

            List<MessageDTO> chatMessages = apiService.getChatMessages(chatId);

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
