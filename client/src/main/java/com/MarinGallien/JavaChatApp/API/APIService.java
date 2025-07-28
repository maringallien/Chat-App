package com.MarinGallien.JavaChatApp.API;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.AuthRequests.LoginRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.AuthRequests.RegisterRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.ChatRequests.*;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.ContactRequests.*;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.MessageRequests.GetChatMessagesRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.UserRequests.*;
import com.MarinGallien.JavaChatApp.UserSession;

import java.util.Set;

public class APIService {

    private final APIClient apiClient;
    private String userId;

    public APIService(APIClient apiClient) {
        this.apiClient = apiClient;
    }

    // ========== AUTHENTICATION METHODS ==========

    public void login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        apiClient.login(request);
        userId = UserSession.getUserId();
    }

    public void register(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest(username, email, password);
        apiClient.register(request);
    }

    // ========== CHAT METHODS ==========

    public void createPrivateChat(String userId2) {
        CreatePcRequest request = new CreatePcRequest(userId, userId2);
        apiClient.createPrivateChat(request);
    }

    public void createGroupChat(Set<String> memberIds, String chatName) {
        CreateGcRequest request = new CreateGcRequest(userId, memberIds, chatName);
        apiClient.createGroupChat(request);
    }

    public void deleteChat(String chatId) {
        DeleteChatRequest request = new DeleteChatRequest(userId, chatId);
        apiClient.deleteChat(request);
    }

    public void addMemberToChat(String memberId, String chatId) {
        AddOrRemoveMemberRequest request = new AddOrRemoveMemberRequest(userId, memberId, chatId);
        apiClient.addMemberToChat(request);
    }

    public void removeMemberFromChat(String memberId, String chatId) {
        AddOrRemoveMemberRequest request = new AddOrRemoveMemberRequest(userId, memberId, chatId);
        apiClient.removeMemberFromChat(request);
    }

    public void getUserChats() {
        GetUserChatsRequest request = new GetUserChatsRequest(userId);
        apiClient.getUserChats(request);
    }

    // ========== CONTACT METHODS ==========

    public void createContact(String contactId) {
        CreateOrRemoveContactRequest request = new CreateOrRemoveContactRequest(userId, contactId);
        apiClient.createContact(request);
    }

    public void removeContact(String contactId) {
        CreateOrRemoveContactRequest request = new CreateOrRemoveContactRequest(userId, contactId);
        apiClient.removeContact(request);
    }

    public void getUserContacts() {
        GetUserContactsRequest request = new GetUserContactsRequest(userId);
        apiClient.getUserContacts(request);
    }

    // ========== MESSAGE METHODS ==========

    public void getChatMessages(String chatId) {
        GetChatMessagesRequest request = new GetChatMessagesRequest(userId, chatId);
        apiClient.getChatMessages(request);
    }

    // ========== USER METHODS ==========

    public void updateUsername(String newUsername) {
        UpdateUnameRequest request = new UpdateUnameRequest(userId, newUsername);
        apiClient.updateUsername(request);
        UserSession.setUsername(newUsername);
    }

    public void updateEmail(String newEmail) {
        UpdateEmailRequest request = new UpdateEmailRequest(userId, newEmail);
        apiClient.updateEmail(request);
        UserSession.setEmail(newEmail);
    }

    public void updatePassword(String oldPassword, String newPassword) {
        UpdatePasswdRequest request = new UpdatePasswdRequest(userId, oldPassword, newPassword);
        apiClient.updatePassword(request);
    }
}