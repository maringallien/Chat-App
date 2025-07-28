package com.MarinGallien.JavaChatApp;

public class CmdParser {

    public void parseAndExecute(String input, ClientManager clientManager) {
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();

        switch (command) {
            // Authentication commands
            case "login":
                if (parts.length >= 3) {
                    clientManager.handleLogin(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: login <email> <password>");
                }
                break;

            case "register":
                if (parts.length >= 4) {
                    clientManager.handleRegister(parts[1], parts[2], parts[3]);
                } else {
                    System.out.println("Usage: register <username> <email> <password>");
                }
                break;

            // Chat commands
            case "chat":
                if (parts.length >= 2) {
                    clientManager.enterChatWithContact(parts[1]);
                } else {
                    System.out.println("Usage: chat <contact_name>");
                }
                break;

            case "create-private":
                if (parts.length >= 2) {
                    clientManager.createPrivateChat(parts[1]);
                } else {
                    System.out.println("Usage: create-private <user_id>");
                }
                break;

            case "create-group":
                if (parts.length >= 3) {
                    // Format: create-group <chat_name> <user_id1> <user_id2> ...
                    String chatName = parts[1];
                    String[] memberIds = Arrays.copyOfRange(parts, 2, parts.length);
                    clientManager.createGroupChat(chatName, memberIds);
                } else {
                    System.out.println("Usage: create-group <chat_name> <user_id1> <user_id2> ...");
                }
                break;

            case "delete-chat":
                if (parts.length >= 2) {
                    clientManager.deleteChat(parts[1]);
                } else {
                    System.out.println("Usage: delete-chat <chat_id>");
                }
                break;

            case "add-member":
                if (parts.length >= 3) {
                    clientManager.addMemberToChat(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: add-member <chat_id> <user_id>");
                }
                break;

            case "remove-member":
                if (parts.length >= 3) {
                    clientManager.removeMemberFromChat(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: remove-member <chat_id> <user_id>");
                }
                break;

            case "chats":
                clientManager.getUserChats();
                break;

            // Contact commands
            case "add-contact":
                if (parts.length >= 2) {
                    clientManager.addContact(parts[1]);
                } else {
                    System.out.println("Usage: add-contact <user_id>");
                }
                break;

            case "remove-contact":
                if (parts.length >= 2) {
                    clientManager.removeContact(parts[1]);
                } else {
                    System.out.println("Usage: remove-contact <user_id>");
                }
                break;

            case "contacts":
                clientManager.getUserContacts();
                break;

            // Message commands
            case "messages":
                if (parts.length >= 2) {
                    clientManager.getChatMessages(parts[1]);
                } else {
                    System.out.println("Usage: messages <chat_id>");
                }
                break;

            // User update commands
            case "update-username":
                if (parts.length >= 2) {
                    clientManager.updateUsername(parts[1]);
                } else {
                    System.out.println("Usage: update-username <new_username>");
                }
                break;

            case "update-email":
                if (parts.length >= 2) {
                    clientManager.updateEmail(parts[1]);
                } else {
                    System.out.println("Usage: update-email <new_email>");
                }
                break;

            case "update-password":
                if (parts.length >= 3) {
                    clientManager.updatePassword(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: update-password <old_password> <new_password>");
                }
                break;

            // Utility commands
            case "help":
                clientManager.showHelp();
                break;

            default:
                System.out.println("Unknown command: " + command + ". Type 'help' for available commands.");
                break;
        }
    }
}