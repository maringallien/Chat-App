package com.MarinGallien.JavaChatApp;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
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
        if (chats.isEmpty()) {
            System.out.println("No chats found.");
        } else {
            for (ChatDTO chat : chats) {
                String chatName = chat.getChatName();
                System.out.println("- " + chatName + " (" + chat.getChatType() + ")");
            }
        }
    }

    public void showMessages(List<MessageDTO> messages) {
        System.out.println("=== Chat Messages ===");
        if (messages.isEmpty()) {
            System.out.println("No messages found.");
        } else {
            for (MessageDTO message : messages) {
                System.out.println("[" + message.getSenderId() + "]: " + message.getContent());
            }
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
        System.out.println("Available commands:");
        System.out.println("  login <email> <password>     - Login to your account");
        System.out.println("  register <username> <email> <password> - Create new account");
        System.out.println("  chat <contact_name>          - Start chat with contact");
        System.out.println("  contacts                     - Show your contacts");
        System.out.println("  chats                        - Show your chats");
        System.out.println("  add <user_id>                - Add contact by user ID");
        System.out.println("  remove <user_id>             - Remove contact by user ID");
        System.out.println("  create <contact_name>        - Create private chat");
        System.out.println("  messages <chat_name>         - Show chat messages");
        System.out.println("  help                         - Show this help");
        System.out.println("  exit                         - Exit application");
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
