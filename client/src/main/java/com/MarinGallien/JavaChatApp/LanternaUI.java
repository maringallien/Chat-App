package com.MarinGallien.JavaChatApp;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.FileDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import com.MarinGallien.JavaChatApp.WebSocket.ChatService;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class LanternaUI implements ChatService.MessageListener {

    private final Terminal terminal;
    private final Screen screen;
    private final Scanner scanner;

    // State
    private boolean inChatMode = false;
    private String currentChatName;
    private String currentUser = "Guest";
    private boolean isOnline = false;

    public LanternaUI() throws IOException {
        this.terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(terminal);
        this.scanner = new Scanner(System.in);
        screen.startScreen();
        screen.clear();
    }

    public void cleanup() {
        try {
            if (screen != null) {
                screen.stopScreen();
            }
            if (terminal != null) {
                terminal.close();
            }
        } catch (IOException e) {
            System.err.println("Error cleaning up UI: " + e.getMessage());
        }
    }

    // ========== VISUAL DISPLAY METHODS ==========

    public void showWelcome() {
        clearScreen();
        printBox("TCHAT", 60, new String[]{
                "      ████████╗ ██████╗██╗  ██╗ █████╗ ████████╗",
                "      ╚══██╔══╝██╔════╝██║  ██║██╔══██╗╚══██╔══╝",
                "         ██║   ██║     ███████║███████║   ██║   ",
                "         ██║   ██║     ██╔══██║██╔══██║   ██║   ",
                "         ██║   ╚██████╗██║  ██║██║  ██║   ██║   ",
                "         ╚═╝    ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ",
                "",
                "                     Welcome to TCHAT!",
                "                   Type 'help' for commands"
        });

        printStatusBar("Status: Starting up...", "");
    }

    public void showChatInterface(String chatName) {
        clearScreen();
        String onlineIndicator = isOnline ? "●" : "○";
        String headerTitle = "Chat with " + chatName;

        printChatBox(headerTitle, currentUser + "! " + onlineIndicator + " " + (isOnline ? "Online" : "Offline"));
        printStatusBar("[F] File  [D] Download File [H] Home  [B] Back  [Q] Quit", "");
    }

    public void showHelp() {
        clearScreen();
        printBox("Available Commands", 80, new String[]{
                "=== Authentication ===",
                "  login <email> <password>                    - Login to your account",
                "  register <username> <email> <password>      - Create new account",
                "",
                "=== Chat Management ===",
                "  chat -g <group_name>                        - Join group chat",
                "  chat -p <contact_name>                      - Start private chat",
                "  create-pc <contact_username>                - Create private chat",
                "  create-gc <chat_name> <contact1> <contact2> - Create group chat",
                "  delete-chat <chat_name>                     - Delete a chat",
                "  chats                                       - Show your chats",
                "  messages <chat_name>                        - Show chat messages",
                "",
                "=== Contact Management ===",
                "  add-contact <contact_username>              - Add contact",
                "  remove-contact <contact_username>           - Remove contact",
                "  contacts                                    - Show your contacts",
                "",
                "=== File Management ===",
                "  upload-file <chatname> <filepath>           - Upload file to chat",
                "  download-file <chatname> <filename> <path>  - Download file",
                "  get-files <chatname>                        - List files in chat",
                "",
                "=== Utility ===",
                "  help                                        - Show this help",
                "  exit                                        - Exit application"
        });

        printStatusBar("Press Enter to continue...", "");
    }

    // ========== CHAT MODE MANAGEMENT ==========

    public void enterChatMode(String chatName) {
        this.inChatMode = true;
        this.currentChatName = chatName;
        showChatInterface(chatName);
        System.out.println();
        System.out.println("=== Entered chat: " + chatName + " ===");
        System.out.println("Type messages to send. Type '/exit' to leave.");
        showChatPrompt();
    }

    public void exitChatMode() {
        this.inChatMode = false;
        this.currentChatName = null;
        clearScreen();
        showWelcome();
        System.out.println("=== Exited chat ===");
        showMainPrompt();
    }

    public void showChatPrompt() {
        System.out.print("┌─ Message: ");
    }

    public void showMainPrompt() {
        System.out.print("┌─ Command: ");
    }

    public void showSentMessage(String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        System.out.println("│  [" + time + "] You: " + message);
    }

    public void showCommand(String command) {
        System.out.println("│  > " + command);
    }

    // ========== MESSAGE LISTENER IMPLEMENTATION ==========

    @Override
    public void onMessageReceived(String senderId, String message) {
        String currentUserId = UserSession.getInstance().getUserId();
        if (currentUserId != null && currentUserId.equals(senderId)) {
            return;
        }

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        System.out.println("│  [" + time + "] " + senderId + ": " + message);

        if (inChatMode) {
            showChatPrompt();
        } else {
            showMainPrompt();
        }
    }

    @Override
    public void onStatusChanged(String userId, OnlineStatus status) {
        String statusText = status == OnlineStatus.ONLINE ? "came online" : "went offline";
        System.out.println("│  📱 " + userId + " " + statusText);

        if (inChatMode) {
            showChatPrompt();
        } else {
            showMainPrompt();
        }
    }

    @Override
    public void onConnectionChanged(boolean connected) {
        this.isOnline = connected;
        String status = connected ? "Connected to chat server" : "Disconnected from chat server";
        String icon = connected ? "🟢" : "🔴";
        System.out.println("│  " + icon + " " + status);

        if (inChatMode) {
            showChatPrompt();
        } else {
            showMainPrompt();
        }
    }

    @Override
    public void onError(String error) {
        System.out.println("│  ❌ " + error);
        if (inChatMode) {
            showChatPrompt();
        } else {
            showMainPrompt();
        }
    }

    // ========== DISPLAY METHODS ==========

    public void showContacts(List<ContactDTO> contacts) {
        clearScreen();
        String[] contactLines = new String[contacts.size() + 2];
        contactLines[0] = "=== Your Contacts ===";
        contactLines[1] = "";

        if (contacts.isEmpty()) {
            contactLines[2] = "No contacts found.";
        } else {
            for (int i = 0; i < contacts.size(); i++) {
                ContactDTO contact = contacts.get(i);
                String indicator = contact.getOnlineStatus() == OnlineStatus.ONLINE ? "🟢" : "⚪";
                contactLines[i + 2] = "  " + indicator + " " + contact.getUsername();
            }
        }

        printBox("Contacts", 60, contactLines);
        printStatusBar("Press Enter to continue...", "");
        scanner.nextLine();
        clearScreen();
        showWelcome();
        showMainPrompt();
    }

    public void showChats(List<ChatDTO> chats) {
        clearScreen();
        String[] chatLines = new String[chats.size() + 2];
        chatLines[0] = "=== Your Chats ===";
        chatLines[1] = "";

        for (int i = 0; i < chats.size(); i++) {
            ChatDTO chat = chats.get(i);
            chatLines[i + 2] = "  - " + chat.getChatName() + " (" + chat.getChatType() + ")";
        }

        printBox("Chat List", 60, chatLines);
        printStatusBar("Press Enter to continue...", "");
        scanner.nextLine();
        clearScreen();
        showWelcome();
        showMainPrompt();
    }

    public void showMessages(List<MessageDTO> messages) {
        System.out.println("│  === Chat Messages ===");
        for (MessageDTO message : messages) {
            System.out.println("│  [" + message.getSenderId() + "]: " + message.getContent());
        }
        System.out.println("│");
    }

    public void showFiles(List<FileDTO> files) {
        System.out.println("│  === Chat Files ===");
        for (FileDTO file : files) {
            System.out.println("│  📄 " + file.getFilename());
        }
        System.out.println("│");
    }

    // ========== STATUS MESSAGES ==========

    public void showLoginSuccess(String username) {
        this.currentUser = username;
        this.isOnline = true;
        clearScreen();
        printSuccessBox("Login Successful", "Welcome back, " + username + "!");
        printStatusBar("Press Enter to continue...", "");
        scanner.nextLine();
        clearScreen();
        showWelcome();
        showMainPrompt();
    }

    public void showLoginFailure() {
        clearScreen();
        printErrorBox("Login Failed", "Please check your credentials and try again.");
        printStatusBar("Press Enter to continue...", "");
        scanner.nextLine();
        clearScreen();
        showWelcome();
        showMainPrompt();
    }

    public void showRegistrationSuccess() {
        clearScreen();
        printSuccessBox("Account Created", "Registration successful! You can now login.");
        printStatusBar("Press Enter to continue...", "");
        scanner.nextLine();
        clearScreen();
        showWelcome();
        showMainPrompt();
    }

    public void showRegistrationFailure() {
        clearScreen();
        printErrorBox("Registration Failed", "Please try again with different credentials.");
        printStatusBar("Press Enter to continue...", "");
        scanner.nextLine();
        clearScreen();
        showWelcome();
        showMainPrompt();
    }

    public void showError(String error) {
        System.out.println("│  ❌ " + error);
    }

    public void showSuccess(String message) {
        System.out.println("│  ✓ " + message);
    }

    public void showInfo(String message) {
        System.out.println("│  ℹ️  " + message);
    }

    public void showNotLoggedIn() {
        System.out.println("│  ❌ Please login first.");
    }

    public void showChatNotFound(String chatName) {
        System.out.println("│  ❌ Chat '" + chatName + "' not found.");
    }

    public void showContactAdded(String contactName) {
        System.out.println("│  ✓ Added " + contactName + " to your contacts.");
    }

    public void showContactRemoved(String contactName) {
        System.out.println("│  ✓ Removed " + contactName + " from your contacts.");
    }

    public void showPrivateChatCreated(String contactName) {
        System.out.println("│  ✓ Created private chat with " + contactName);
    }

    public void showGroupChatCreated(String chatName) {
        System.out.println("│  ✓ Created group chat: " + chatName);
    }

    public void showGoodbye() {
        clearScreen();
        printBox("Goodbye!", 40, new String[]{
                "Thank you for using Java Chat Client!",
                "",
                "See you next time! 👋"
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ========== VISUAL HELPER METHODS ==========

    private void clearScreen() {
        try {
            screen.clear();
            screen.refresh();
        } catch (IOException e) {
            System.err.println("Error clearing screen: " + e.getMessage());
        }
    }

    private void printBox(String title, int width, String[] content) {
        // Top border
        System.out.println("┌" + "─".repeat(width - title.length() - 4) + " " + title + " " + "─".repeat(width - title.length() - 4) + "┐");

        // Content
        for (String line : content) {
            int padding = width - line.length() - 4;
            String leftPad = " ".repeat(Math.max(0, padding / 2));
            String rightPad = " ".repeat(Math.max(0, padding - leftPad.length()));
            System.out.println("│  " + leftPad + line + rightPad + "  │");
        }

        // Bottom border
        System.out.println("└" + "─".repeat(width - 2) + "┘");
    }

    private void printChatBox(String title, String userInfo) {
        System.out.println("┌" + "─".repeat(30) + " " + title + " " + "─".repeat(30) + "┐");
        System.out.println("│ " + userInfo + " ".repeat(Math.max(0, 78 - userInfo.length())) + "│");
        System.out.println("├" + "─".repeat(78) + "┤");
        System.out.println("│  ┌" + "─".repeat(74) + "┐  │");
    }

    private void printSuccessBox(String title, String message) {
        System.out.println("┌" + "─".repeat(35) + " " + title + " " + "─".repeat(35) + "┐");
        System.out.println("│" + " ".repeat(80) + "│");
        System.out.println("│  ✓ " + message + " ".repeat(Math.max(0, 76 - message.length())) + "│");
        System.out.println("│" + " ".repeat(80) + "│");
        System.out.println("└" + "─".repeat(80) + "┘");
    }

    private void printErrorBox(String title, String message) {
        System.out.println("┌" + "─".repeat(35) + " " + title + " " + "─".repeat(35) + "┐");
        System.out.println("│" + " ".repeat(80) + "│");
        System.out.println("│  ❌ " + message + " ".repeat(Math.max(0, 75 - message.length())) + "│");
        System.out.println("│" + " ".repeat(80) + "│");
        System.out.println("└" + "─".repeat(80) + "┘");
    }

    private void printStatusBar(String leftText, String rightText) {
        int totalWidth = 80;
        int rightTextLength = rightText.length();
        int leftSpace = totalWidth - rightTextLength - 2;
        String paddedLeft = (leftText.length() > leftSpace) ?
                leftText.substring(0, leftSpace - 3) + "..." : leftText;
        String padding = " ".repeat(Math.max(0, leftSpace - paddedLeft.length()));

        System.out.println("  " + paddedLeft + padding + rightText);
    }

    // ========== GETTERS ==========

    public boolean isInChatMode() {
        return inChatMode;
    }

    public String getCurrentChatName() {
        return currentChatName;
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
    }
}
