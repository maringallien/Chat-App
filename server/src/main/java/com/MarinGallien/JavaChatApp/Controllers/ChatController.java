package com.MarinGallien.JavaChatApp.Controllers;


import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.ChatRequests.*;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.ChatResponses.GetUserChatsResponse;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.GenericResponse;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Chat;
import com.MarinGallien.JavaChatApp.Services.ChatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/private")
    public ResponseEntity<GenericResponse> createPrivateChat(
            @Valid @RequestBody CreatePcRequest request,
            BindingResult bindingResult) {

        try {
            // Check if input has any errors and send error message if it does
            if (bindingResult.hasErrors()) {
                logger.warn("Invalid request to create private chat: input parameter(s) null or empty");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Invalid request parameters"));
            }

            // Delegate to ChatService
            Chat createdChat = chatService.createPrivateChat(request.userId1(), request.userId2());

            // If chat creation failed, send error message
            if (createdChat == null) {
                logger.error("Failed to create private chat");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Failed to create private chat"));
            }

            // Send success response message
            logger.info("Successfully create new private chat {}", createdChat.getChatId());
            return ResponseEntity.ok().body(new GenericResponse(true, "Private chat created successfully"));

        } catch (Exception e) {
            logger.error("Error handling request to create private chat: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal server error"));
        }
    }

    @PostMapping("/group")
    public ResponseEntity<GenericResponse> createGroupChat(
            @Valid @RequestBody CreateGcRequest request,
            BindingResult bindingResult) {

        try {
            // Check if input has any errors and send error message if it does
            if (bindingResult.hasErrors()) {
                logger.warn("Invalid request to create group chat: input parameter(s) null or empty");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Invalid request parameters"));
            }

            // Delegate to ChatService
            Chat createdChat = chatService.createGroupChat(request.creatorId(), request.memberIds(), request.chatName());

            // If chat creation failed, send error message
            if (createdChat == null) {
                logger.error("Failed to create group chat");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Failed to create private chat"));
            }

            // Send success response message
            logger.info("Successfully create new group chat {}", createdChat.getChatId());
            return ResponseEntity.ok().body(new GenericResponse(true, "Private chat created successfully"));

        } catch (Exception e) {
            logger.error("Failed to create group chat");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GenericResponse(false, "Internal server error"));
        }
    }

    @DeleteMapping
    public ResponseEntity<GenericResponse> deleteChat(
            @Valid @RequestBody DeleteChatRequest request,
            BindingResult bindingResult) {

        try {
            // Check if input has any errors and send error message if it does
            if (bindingResult.hasErrors()) {
                logger.warn("Invalid request to delete chat: input parameter(s) null or empty");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Invalid request parameters"));
            }

            // Delegate to ChatService
            boolean deleted = chatService.deleteChat(request.creatorId(), request.chatId());

            // If chat creation failed, send error message
            if (!deleted) {
                logger.error("Failed to delete chat");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Failed to create private chat"));
            }

            // Send success response message
            logger.info("Successfully deleted chat {}", request.chatId());
            return ResponseEntity.ok().body(new GenericResponse(true, "Private chat created successfully"));

        } catch (Exception e) {
            logger.error("Failed to delete chat");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal server error"));
        }
    }

    @PostMapping("/member/add")
    public ResponseEntity<GenericResponse> addMemberToChat(
            @Valid @RequestBody AddOrRemoveMemberRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has any errors
            if (bindingResult.hasErrors()) {
                logger.warn("Invalid request to add member to chat: input parameter(s) null or empty");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Invalid request parameters"));
            }

            // Delegate to chatService
            boolean added = chatService.addMember(request.creatorId(), request.userId(), request.chatId());

            //If chat creation failed, send error message
            if (!added) {
                logger.error("Failed to add member to chat");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Failed to create private chat"));
            }

            // Send success response message
            logger.info("Successfully added member to chat {}", request.chatId());
            return ResponseEntity.ok().body(new GenericResponse(true, "Private chat created successfully"));

        } catch (Exception e) {
            logger.error("Failed to add member to chat");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal server error"));
        }
    }

    @PostMapping("/member/remove")
    public ResponseEntity<GenericResponse> removeMemberFromChat(
            @Valid @RequestBody AddOrRemoveMemberRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has any errors
            if (bindingResult.hasErrors()) {
                logger.warn("Invalid request to remove member from chat: input parameter(s) null or empty");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Invalid request parameters"));
            }

            // Delegate to chatService
            boolean removed = chatService.removeMember(request.creatorId(), request.userId(), request.chatId());

            // If chat creation failed, send error message
            if (!removed) {
                logger.error("Failed to remove member from chat");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Failed to create private chat"));
            }

            // Send success response message
            logger.info("Successfully removed member from chat {}", request.chatId());
            return ResponseEntity.ok().body(new GenericResponse(true, "Private chat created successfully"));

        } catch (Exception e) {
            logger.error("Failed to remove member to chat");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal server error"));
        }
    }

    @GetMapping("/chats")
    public ResponseEntity<GetUserChatsResponse> getUserChats(
            @Valid @RequestBody GetUserChatsRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has any errors
            if (bindingResult.hasErrors()) {
                logger.warn("Invalid request to retrieve user's chats: input parameter(s) null or empty");
                return ResponseEntity.badRequest()
                        .body(new GetUserChatsResponse(false, "Invalid request parameters", List.of()));
            }

            // Delegate to chatService
            List<ChatDTO> userChats = chatService.getUserChats(request.userId());

            // Handle no chats found
            if (userChats == null || userChats.isEmpty()) {
                logger.warn("No chats were found");
                return ResponseEntity.badRequest()
                        .body(new GetUserChatsResponse(false, "No chats found", List.of()));
            }

            logger.info("Sending back user's chats");
            return ResponseEntity.ok()
                    .body(new GetUserChatsResponse(true, "Successfully retrieved user's chats", userChats));

        } catch (Exception e) {
            logger.error("Failed to retrieve user's chats");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GetUserChatsResponse(false, "Internal server error", List.of()));
        }
    }


}
