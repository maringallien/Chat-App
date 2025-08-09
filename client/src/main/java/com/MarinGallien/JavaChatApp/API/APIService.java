package com.MarinGallien.JavaChatApp.API;

import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.GetterRequests.*;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.GetterResponses.*;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.FileDTO;
import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.HTTPRequests.*;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.HTTPResponses.*;
import com.MarinGallien.JavaChatApp.UserSession;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

public class APIService {

    private final APIClient apiClient;

    public APIService(APIClient apiClient) {
        this.apiClient = apiClient;
    }


    // ========== AUTHENTICATION METHODS ==========

    public boolean login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        LoginResponse response = apiClient.login(request);

        if (response.success()) {
            UserSession.getInstance().setUserId(response.userId());
            UserSession.getInstance().setUsername(response.username());

            // Set JWT token in API client for authenticated requests
            apiClient.setJwtToken(response.JwtToken());
            return true;
        }
        return false;
    }

    public boolean register(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest(username, email, password);
        GenericResponse response = apiClient.register(request);
        return response.success();
    }


    // ========== CHAT METHODS ==========

    public boolean createPrivateChat(String userId2) {
        CreatePcRequest request = new CreatePcRequest(getLocalUserId(), userId2);
        GenericResponse response = apiClient.createPrivateChat(request);
        return response.success();
    }

    public boolean createGroupChat(Set<String> memberIds, String chatName) {
        CreateGcRequest request = new CreateGcRequest(getLocalUserId(), memberIds, chatName);
        GenericResponse response = apiClient.createGroupChat(request);
        return response.success();
    }

    public boolean deleteChat(String chatId) {
        DeleteChatRequest request = new DeleteChatRequest(getLocalUserId(), chatId);
        GenericResponse response = apiClient.deleteChat(request);
        return response.success();
    }

    public boolean addMemberToChat(String memberId, String chatId) {
        AddOrRemoveMemberRequest request = new AddOrRemoveMemberRequest(getLocalUserId(), memberId, chatId);
        GenericResponse response = apiClient.addMemberToChat(request);
        return response.success();
    }

    public boolean removeMemberFromChat(String memberId, String chatId) {
        AddOrRemoveMemberRequest request = new AddOrRemoveMemberRequest(getLocalUserId(), memberId, chatId);
        GenericResponse response = apiClient.removeMemberFromChat(request);
        return response.success();
    }

    public List<ChatDTO> getUserChats() {
        GetUserChatsRequest request = new GetUserChatsRequest(getLocalUserId());
        GetUserChatsResponse response = apiClient.getUserChats(request);

        if (response.success() && response.chats() != null) {
            return response.chats();
        }
        return List.of();
    }


    // ========== CONTACT METHODS ==========

    public boolean createContact(String contactId) {
        CreateOrRemoveContactRequest request = new CreateOrRemoveContactRequest(getLocalUserId(), contactId);
        GenericResponse response = apiClient.createContact(request);
        return response.success();
    }

    public boolean removeContact(String contactId) {
        CreateOrRemoveContactRequest request = new CreateOrRemoveContactRequest(getLocalUserId(), contactId);
        GenericResponse response = apiClient.removeContact(request);
        return response.success();
    }

    public List<ContactDTO> getUserContacts() {
        GetUserContactsRequest request = new GetUserContactsRequest(getLocalUserId());
        GetUserContactsResponse response = apiClient.getUserContacts(request);

        if (response.success() && response.contacts() != null) {
            return response.contacts();
        }
        return List.of(); // Return empty list on failure
    }


    // ========== MESSAGE METHODS ==========

    public List<MessageDTO> getChatMessages(String chatId) {
        GetChatMessagesRequest request = new GetChatMessagesRequest(getLocalUserId(), chatId);
        GetChatMessagesResponse response = apiClient.getChatMessages(request);

        if (response.success() && response.messages() != null) {
            return response.messages();
        }
        return List.of(); // Return empty list on failure
    }


    // ========== USER METHODS ==========

    public boolean updateUsername(String newUsername) {
        UpdateUnameRequest request = new UpdateUnameRequest(getLocalUserId(), newUsername);
        GenericResponse response = apiClient.updateUsername(request);

        if (response.success()) {
            UserSession.getInstance().setUsername(newUsername);
            return true;
        }
        return false;
    }

    public boolean updateEmail(String newEmail) {
        UpdateEmailRequest request = new UpdateEmailRequest(getLocalUserId(), newEmail);
        GenericResponse response = apiClient.updateEmail(request);

        if (response.success()) {
            UserSession.getInstance().setEmail(newEmail);
            return true;
        }
        return false;
    }

    public boolean updatePassword(String oldPassword, String newPassword) {
        UpdatePasswdRequest request = new UpdatePasswdRequest(getLocalUserId(), oldPassword, newPassword);
        GenericResponse response = apiClient.updatePassword(request);
        return response.success();
    }


    // ========== FILE METHODS ==========

    public boolean uploadFile(String chatId, File file) {
        GenericResponse response = apiClient.uploadFile(getLocalUserId(), chatId, file);
        return response.success();
    }

    public boolean downloadFile(String chatId, String fileId, String filepath) {
        byte[] fileData = apiClient.downloadFile(getLocalUserId(), chatId, fileId);

        if (fileData == null || fileData.length == 0) {
            return false;
        }

        // Create target filepath
        File targetFile = new File(filepath);

        // Create parent directories if they don't exist
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // write byte array to file
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(fileData);
            fos.flush();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public List<FileDTO> getChatFiles(String chatId) {
        try {
            GetChatFilesRequest request = new GetChatFilesRequest(getLocalUserId(), chatId);
            GetChatFilesResponse response = apiClient.getChatFiles(request);
            return response.files();
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean deleteFile(String chatId, String fileId) {
        try {
            DeleteFileRequest request = new DeleteFileRequest(getLocalUserId(), chatId, fileId);
            GenericResponse response = apiClient.deleteFile(request);
            return response.success();
        } catch (Exception e){
            return false;
        }
    }

    // ========== NETWORK GETTER METHODS ==========

    public String getUserIdFromUsername(String username) {
        UserIdRequest request = new UserIdRequest(username);
        UserIdResponse response = apiClient.getUserIdFromUsername(request);
        return response.userId();
    }

    public List<String> getUserIdsFromUsernames(List<String> usernames) {
        UserIdsRequest request = new UserIdsRequest(usernames);
        UserIdsResponse response = apiClient.getUserIdsFromUsernames(request);
        return response.userIds();
    }

    public String getFileIdFromFilename(String filename, String chatId) {
        FileIdRequest request = new FileIdRequest(filename, chatId);
        FileIdResponse response = apiClient.getFileIdFromFilename(request);
        return response.fileId();
    }

    public String getChatIdFromChatName(String chatName) {
        ChatIdRequest request = new ChatIdRequest(chatName);
        ChatIdResponse response = apiClient.getChatIdFromChatName(request);
        return response.chatId();
    }


    // ========== UTILITY METHODS ==========
    public void logout() {
        apiClient.logout();
    }

    private String getLocalUserId() {
        return UserSession.getInstance().getUserId();
    }
}
