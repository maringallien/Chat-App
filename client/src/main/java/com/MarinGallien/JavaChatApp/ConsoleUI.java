package com.MarinGallien.JavaChatApp;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.FileDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import com.MarinGallien.JavaChatApp.WebSocket.ChatService;

import java.util.List;

public class ConsoleUI implements ChatService.MessageListener {

    // UI State for display purposes only
    private boolean inChatMode = false;
    private String currentChatName;

    public ConsoleUI() {
    }


    // ========== CHAT MODE DISPLAY MANAGEMENT ==========

    public void enterChatMode(String chatName) {
        this.inChatMode = true;
        this.currentChatName = chatName;

        System.out.println("=== Entered chat: " + chatName + " ===");
        System.out.println("Type messages to send. Type '/exit' to leave.");
    }

    public void exitChatMode() {
        this.inChatMode = false;
        this.currentChatName = null;
        System.out.println("=== Exited chat ===");
    }

    public void showChatPrompt() {
        if (inChatMode) {
            System.out.print("[" + currentChatName + "] > ");
        } else {
            System.out.print("> ");
        }
    }

    public void showSentMessage(String message) {
        System.out.println("[You]: " + message);
    }


    // ========== ChatService.MessageListener IMPLEMENTATION ==========
    // These methods are called by ChatService when WebSocket events occur

    @Override
    public void onMessageReceived(String senderId, String message) {
        String currentUserId = UserSession.getInstance().getUserId();
        if (currentUserId != null && currentUserId.equals(senderId)) {
            // This is your own message echoed back - don't display it
            return;
        }

        // Display incoming message immediately
        System.out.println("\n[" + senderId + "]: " + message);
        showChatPrompt(); // Show prompt again after message
    }

    @Override
    public void onStatusChanged(String userId, OnlineStatus status) {
        String statusText = status == OnlineStatus.ONLINE ? "came online" : "went offline";
        System.out.println("\n📱 " + userId + " " + statusText);
        showChatPrompt(); // Show prompt again after status update
    }

    @Override
    public void onConnectionChanged(boolean connected) {
        String status = connected ? "Connected to" : "Disconnected from";
        System.out.println("🔗 " + status + " chat server");
    }

    @Override
    public void onError(String error) {
        System.err.println("❌ " + error);
    }


    // ========== DISPLAY METHODS FOR API DATA ==========
    // These methods are called by ClientManager to display data

    public void showContacts(List<ContactDTO> contacts) {
        System.out.println("=== Your Contacts ===");
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
        } else {
            for (ContactDTO contact : contacts) {
                String indicator = contact.getOnlineStatus() == OnlineStatus.ONLINE ? "🟢" : "⚪";
                System.out.println(indicator + " " + contact.getUsername());
            }
        }
    }

    public void showChats(List<ChatDTO> chats) {
        System.out.println("=== Your Chats ===");
        for (ChatDTO chat : chats) {
            String chatName = chat.getChatName();
            System.out.println("- " + chatName + " (" + chat.getChatType() + ")");
        }
    }

    public void showMessages(List<MessageDTO> messages) {
        System.out.println("=== Chat Messages ===");
        for (MessageDTO message : messages) {
            System.out.println("[" + message.getSenderId() + "]: " + message.getContent());
        }
    }

    public void showFiles(List<FileDTO> files) {
        System.out.println("=== Chat Messages ===");
        for (FileDTO file : files) {
            System.out.println(file.getFilename());
        }
    }

    public void showLoginSuccess(String username) {
        System.out.println("✓ Login successful! Welcome " + username);
    }

    public void showLoginFailure() {
        System.out.println("❌ Login failed. Please check your credentials.");
    }

    public void showRegistrationSuccess() {
        System.out.println("✓ Registration successful! You can now login.");
    }

    public void showRegistrationFailure() {
        System.out.println("❌ Registration failed. Please try again.");
    }

    public void showChatNotFound(String chatName) {
        System.out.println("❌ Chat '" + chatName + "' not found.");
    }

    public void showContactAdded(String contactName) {
        System.out.println("✓ Added " + contactName + " to your contacts.");
    }

    public void showContactRemoved(String contactName) {
        System.out.println("✓ Removed " + contactName + " from your contacts.");
    }

    public void showPrivateChatCreated(String contactName) {
        System.out.println("✓ Created private chat with " + contactName);
    }

    public void showGroupChatCreated(String chatName) {
        System.out.println("✓ Created group chat: " + chatName);
    }

    public void showError(String error) {
        System.err.println("❌ " + error);
    }

    public void showSuccess(String message) {
        System.out.println("✓ " + message);
    }

    public void showInfo(String message) {
        System.out.println("ℹ️  " + message);
    }

    public void showNotLoggedIn() {
        System.out.println("❌ Please login first.");
    }

    public void showWelcome() {
        System.out.println("=== Java Chat Client ===");
        System.out.println("Type 'help' for commands");
        System.out.println("========================");
    }

    public void showHelp() {
        System.out.println("=== Available commands ===");
        System.out.println();

        // Authentication commands
        System.out.println("=== Authentication ===");
        System.out.println("  login <email> <password>                    - Login to your account");
        System.out.println("  register <username> <email> <password>      - Create new account");
        System.out.println();

        // Chat commands
        System.out.println("=== Chat Management ===");
        System.out.println("  chat -g <group_name>                        - Join group chat");
        System.out.println("  chat -p <contact_name>                      - Start private chat");
        System.out.println("  create-pc <contact_username>                - Create private chat");
        System.out.println("  create-gc <chat_name> <contact1> <contact2> - Create group chat with contacts");
        System.out.println("  delete-chat <chat_name>                     - Delete a chat");
        System.out.println("  chats                                       - Show your chats");
        System.out.println("  messages <chat_name>                        - Show chat messages");
        System.out.println();

        // Group chat management
        System.out.println("=== Group Chat Management ===");
        System.out.println("  add-member <chat_name> <contact_username>   - Add member to group chat");
        System.out.println("  remove-member <chat_name> <contact_username>- Remove member from group chat");
        System.out.println();

        // Contact commands
        System.out.println("=== Contact Management ===");
        System.out.println("  add-contact <contact_username>              - Add contact by username");
        System.out.println("  remove-contact <contact_username>           - Remove contact by username");
        System.out.println("  contacts                                    - Show your contacts");
        System.out.println();

        // File commands
        System.out.println("=== File Management ===");
        System.out.println("  upload-file <chatname> <filepath>           - Upload file to chat");
        System.out.println("  download-file <chatname> <filename> <path>  - Download file from chat");
        System.out.println("  get-files <chatname>                        - List files in chat");
        System.out.println();

        // User update commands
        System.out.println("=== Account Settings ===");
        System.out.println("  update-username <new_username>              - Update your username");
        System.out.println("  update-email <new_email>                    - Update your email");
        System.out.println("  update-password <old_password> <new_password> - Update your password");
        System.out.println();

        // Utility commands
        System.out.println("=== Utility ===");
        System.out.println("  help                                        - Show this help");
        System.out.println("  exit                                        - Exit application");
        System.out.println();

        // Chat mode info
        System.out.println("=== In Chat Mode ===");
        System.out.println("  Type messages to send them");
        System.out.println("  /exit                                       - Leave current chat");
    }
    public void showGoodbye() {
        System.out.println("Goodbye!");
    }


    // ========== GETTERS ==========

    public boolean isInChatMode() {
        return inChatMode;
    }

    public String getCurrentChatName() {
        return currentChatName;
    }
}
