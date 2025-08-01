package com.MarinGallien.JavaChatApp.API;

import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests.ChatIdRequest;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests.FileIdRequest;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests.UserIdRequest;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Requests.UserIdsRequest;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses.ChatIdResponse;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses.FileIdResponse;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses.UserIdResponse;
import com.MarinGallien.JavaChatApp.DTOs.GetterMessages.Responses.UserIdsResponse;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.AuthRequests.LoginRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.AuthRequests.RegisterRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.ChatRequests.*;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.ContactRequests.*;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.MessageRequests.GetChatMessagesRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.UserRequests.*;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.AuthResponses.LoginResponse;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.ChatResponses.GetUserChatsResponse;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.ContactResponses.GetUserContactsResponse;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.GenericResponse;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.MessageReponses.GetChatMessagesResponse;

import com.MarinGallien.JavaChatApp.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Component
public class APIClient {
    private static final Logger logger = LoggerFactory.getLogger(APIClient.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private String jwtToken;

    public APIClient() {
        this.baseUrl = UserSession.getHttpBaseUrl();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();

        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    // ========== AUTHENTICATION METHODS ==========

    // Authenticate user with email and password
    public LoginResponse login(LoginRequest request) {
        try {
            String jsonBody = objectMapper.writeValueAsString(request);

            // Create HTTP request to login endpoint
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Send request and get response
            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            // Deserialize response from JSON
            LoginResponse loginResponse = objectMapper.readValue(response.body(), LoginResponse.class);

            // Init userSession
            if (loginResponse.success()) {
                UserSession.initUserSession(loginResponse.userId(), loginResponse.JwtToken());
            }

            return loginResponse;

        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return new LoginResponse(false, "Login failed: " + e.getMessage(), null, null);
        }
    }

    // Register a new user account
    public GenericResponse register(RegisterRequest request) {
        try {
            String jsonBody = objectMapper.writeValueAsString(request);

            // Build HTTP request to register endpoint
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Send request and get response
            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            // Deserialize and return response
            return objectMapper.readValue(response.body(), GenericResponse.class);

        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return new GenericResponse(false, "Registration failed: " + e.getMessage());
        }
    }

    // ========== CHAT METHODS ==========

    public GenericResponse createPrivateChat(CreatePcRequest request) {
        try {
            return sendAuthenticatedRequest("/api/chat/private", request, GenericResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to create private chat: {}", e.getMessage());
            return new GenericResponse(false, "Failed to create private chat: " + e.getMessage());
        }
    }

    public GenericResponse createGroupChat(CreateGcRequest request) {
        try {
            return sendAuthenticatedRequest("/api/chat/group", request, GenericResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to create group chat: {}", e.getMessage());
            return new GenericResponse(false, "Failed to create group chat: " + e.getMessage());
        }
    }

    public GenericResponse deleteChat(DeleteChatRequest request) {
        try {
            return sendAuthenticatedRequest("/api/chat", request, GenericResponse.class, "DELETE");
        } catch (Exception e) {
            logger.error("Failed to delete chat: {}", e.getMessage());
            return new GenericResponse(false, "Failed to delete chat: " + e.getMessage());
        }
    }

    public GenericResponse addMemberToChat(AddOrRemoveMemberRequest request) {
        try {
            return sendAuthenticatedRequest("/api/chat/member/add", request, GenericResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to add member to chat: {}", e.getMessage());
            return new GenericResponse(false, "Failed to add member to chat: " + e.getMessage());
        }
    }

    public GenericResponse removeMemberFromChat(AddOrRemoveMemberRequest request) {
        try {
            return sendAuthenticatedRequest("/api/chat/member/remove", request, GenericResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to remove member from chat: {}", e.getMessage());
            return new GenericResponse(false, "Failed to remove member from chat: " + e.getMessage());
        }
    }

    public GetUserChatsResponse getUserChats(GetUserChatsRequest request) {
        try {
            return sendAuthenticatedRequest("/api/chat/chats", request, GetUserChatsResponse.class, "GET");
        } catch (Exception e) {
            logger.error("Failed to get user chats: {}", e.getMessage());
            return new GetUserChatsResponse(false, "Failed to get user chats: " + e.getMessage(), null);
        }
    }

    // ========== CONTACT METHODS ==========

    public GenericResponse createContact(CreateOrRemoveContactRequest request) {
        try {
            return sendAuthenticatedRequest("/api/contact/create", request, GenericResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to create contact: {}", e.getMessage());
            return new GenericResponse(false, "Failed to create contact: " + e.getMessage());
        }
    }

    public GenericResponse removeContact(CreateOrRemoveContactRequest request) {
        try {
            return sendAuthenticatedRequest("/api/contact/delete", request, GenericResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to remove contact: {}", e.getMessage());
            return new GenericResponse(false, "Failed to remove contact: " + e.getMessage());
        }
    }

    public GetUserContactsResponse getUserContacts(GetUserContactsRequest request) {
        try {
            return sendAuthenticatedRequest("/api/contact/contacts", request, GetUserContactsResponse.class, "GET");
        } catch (Exception e) {
            logger.error("Failed to get user contacts: {}", e.getMessage());
            return new GetUserContactsResponse(false, "Failed to get user contacts: " + e.getMessage(), null);
        }
    }

    // ========== MESSAGE METHODS ==========

    public GetChatMessagesResponse getChatMessages(GetChatMessagesRequest request) {
        try {
            return sendAuthenticatedRequest("/api/message/messages", request, GetChatMessagesResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to get chat messages: {}", e.getMessage());
            return new GetChatMessagesResponse(false, "Failed to get chat messages: " + e.getMessage(), null);
        }
    }

    // ========== USER METHODS ==========

    public GenericResponse updateUsername(UpdateUnameRequest request) {
        try {
            return sendAuthenticatedRequest("/api/user/update/username", request, GenericResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to update username: {}", e.getMessage());
            return new GenericResponse(false, "Failed to update username: " + e.getMessage());
        }
    }

    public GenericResponse updateEmail(UpdateEmailRequest request) {
        try {
            return sendAuthenticatedRequest("/api/user/update/email", request, GenericResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to update email: {}", e.getMessage());
            return new GenericResponse(false, "Failed to update email: " + e.getMessage());
        }
    }

    public GenericResponse updatePassword(UpdatePasswdRequest request) {
        try {
            return sendAuthenticatedRequest("/api/user/update/password", request, GenericResponse.class, "POST");
        } catch (Exception e) {
            logger.error("Failed to update password: {}", e.getMessage());
            return new GenericResponse(false, "Failed to update password: " + e.getMessage());
        }
    }


    // ========== NETWORK GETTER METHODS ==========
    public UserIdResponse getUserIdFromUsername(UserIdRequest request) {
        try {
            return sendAuthenticatedRequest("/api/user/userId", request, UserIdResponse.class, "GET");
        } catch (Exception e) {
            logger.error("Failed to update email: {}", e.getMessage());
            return new UserIdResponse(false, null);
        }
    }

    public UserIdsResponse getUserIdsFromUsernames(UserIdsRequest request) {
        try {
            return sendAuthenticatedRequest("/api/user/userIds", request, UserIdsResponse.class, "GET");
        } catch (Exception e) {
            logger.error("Failed to update email: {}", e.getMessage());
            return new UserIdsResponse(false, null);
        }
    }

    public FileIdResponse getFileIdFromFilename(FileIdRequest request) {
        try {
            return sendAuthenticatedRequest("/api/fileId", request, FileIdResponse.class, "GET");
        } catch (Exception e) {
            logger.error("Failed to update email: {}", e.getMessage());
            return new FileIdResponse(false, null);
        }
    }

    public ChatIdResponse getChatIdFromChatName(ChatIdRequest request) {
        try {
            return sendAuthenticatedRequest("/api/chatId", request, ChatIdResponse.class, "GET");
        } catch (Exception e) {
            logger.error("Failed to update email: {}", e.getMessage());
            return new ChatIdResponse(false, null);
        }
    }

    // ========== UTILITY METHODS ==========

    private <T, R> R sendAuthenticatedRequest(String endpoint, T requestBody, Class<R> responseClass, String method)
            throws IOException, InterruptedException {

        // Check if user is authenticated before making request
        if (jwtToken == null) {
            throw new IllegalStateException("Not authenticated. Please login first.");
        }

        // Serialize request body to JSON
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // Build basic HTTP request with auth headers
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwtToken);

        // Set HTTP method
        switch (method.toUpperCase()) {
            case "POST":
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonBody));
                break;
            case "DELETE":
                requestBuilder.DELETE();
                break;
            case "GET":
            default:
                requestBuilder.GET();
                break;
        }

        // Build and send request
        HttpRequest httpRequest = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(httpRequest,
                HttpResponse.BodyHandlers.ofString());

        // Deserialize response to specified class type
        return objectMapper.readValue(response.body(), responseClass);
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public boolean isAuthenticated() {
        return jwtToken != null && !jwtToken.trim().isEmpty();
    }

    public void logout() {
        this.jwtToken = null;
    }
}