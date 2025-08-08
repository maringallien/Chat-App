package com.MarinGallien.JavaChatApp;

import java.util.Arrays;
import java.util.List;

public class CmdParser {

    public void parseAndExecute(String input, ClientManager clientManager) {
       // Handle empty input
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        if (clientManager.isInChatMode()) {
            handleChatInput(input.trim(), clientManager);
            return;
        }

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
                if (parts.length < 2) {
                    System.out.println("Usage: hat -g <group_name> OR chat -p <contact_name>");
                    break;
                }

                // Check for flags
                if (parts[1].equals("-g") || parts[1].equals("--group")) {
                    // Group chat mode
                    if (parts.length >= 3) {
                        clientManager.enterGroupChat(parts[2]);
                    } else {
                        System.out.println("Usage: chat -g <group_name>");
                    }
                } else if (parts[1].equals("-p") || parts[1].equals("--private")) {
                    // Private chat mode (explicit)
                    if (parts.length >= 3) {
                        clientManager.enterPrivateChat(parts[2]);
                    } else {
                        System.out.println("Usage: chat -p <contact_name>");
                    }
                }
                break;

            case "create-pc":
                if (parts.length >= 2) {
                    clientManager.createPrivateChat(parts[1]);
                } else {
                    System.out.println("Usage: create-private <contact_username>");
                }
                break;

            case "create-gc":
                if (parts.length >= 3) {
                    // Format: create-group <chat_name> <contact_username1> <contact_username2> ...
                    String chatName = parts[1];
                    List<String> memberIds = Arrays.asList(Arrays.copyOfRange(parts, 2, parts.length));
                    clientManager.createGroupChat(chatName, memberIds);
                } else {
                    System.out.println("Usage: create-group <chat_name> <contact_username1> <contact_username2> ...");
                }
                break;

            case "delete-chat":
                if (parts.length >= 2) {
                    clientManager.deleteChat(parts[1]);
                } else {
                    System.out.println("Usage: delete-chat <chat_name>");
                }
                break;

            case "add-member":
                if (parts.length >= 3) {
                    clientManager.addMemberToChat(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: add-member <chat_name> <contact_username>");
                }
                break;

            case "remove-member":
                if (parts.length >= 3) {
                    clientManager.removeMemberFromChat(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: remove-member <chat_name> <contact_username>");
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
                    System.out.println("Usage: add-contact <contact_username>");
                }
                break;

            case "remove-contact":
                if (parts.length >= 2) {
                    clientManager.removeContact(parts[1]);
                } else {
                    System.out.println("Usage: remove-contact <contact_username>");
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
                    System.out.println("Usage: messages <chat_name>");
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
            case "upload-file":
                if (parts.length >= 3) {
                    clientManager.uploadFile(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: upload-file <chatname> <filepath>");
                }
                break;

            case "download-file":
                if (parts.length >= 4) {
                    clientManager.downloadFile(parts[1], parts[2], parts[3]);
                } else {
                    System.out.println("Usage: download-file <chatname> <filename> <filepath>");
                }
                break;

            case "get-files":
                if (parts.length >= 2) {
                    clientManager.getChatFiles(parts[1]);
                } else {
                    System.out.println("Usage: get-files <chatname>");
                }
                break;

            case "clear":
                clientManager.clearScreen();
                break;

            case "exit":
            case "help":
                clientManager.showHelp();
                break;

            default:
                clientManager.displayError("Unknown command: " + command + ". Type 'help' for available commands.");
                break;
        }
    }

    private void handleChatInput(String input, ClientManager clientManager) {
        if (input.equals("/exit")) {
            clientManager.exitCurrentChat();
        } else {
            clientManager.sendMessage(input);
        }
    }
}