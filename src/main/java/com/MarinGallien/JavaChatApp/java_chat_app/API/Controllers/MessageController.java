package com.MarinGallien.JavaChatApp.java_chat_app.API.Controllers;

import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Requests.MessageRequests.GetChatMessagesRequest;
import com.MarinGallien.JavaChatApp.java_chat_app.DTOs.HTTPMessages.Responses.MessageReponses.GetChatMessagesResponse;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
import com.MarinGallien.JavaChatApp.java_chat_app.Services.MessageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/messages")
    public ResponseEntity<GetChatMessagesResponse> getChatMessages(
            @Valid @RequestBody GetChatMessagesRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has errors
            if (bindingResult.hasErrors()) {
                logger.warn("Failed to retrieve chat messages: input has errors");
                return ResponseEntity.badRequest()
                        .body(new GetChatMessagesResponse(false, "Failed to retrieve chat messages: Input has errors", List.of()));
            }

            // Delegate to MessageService
            List<Message> messages = messageService.getChatMessages(request.userId(), request.chatId());

            // Send response back
            return ResponseEntity.ok()
                    .body(new GetChatMessagesResponse(true, "Successfully retrieved chat messages", messages));

        } catch (Exception e) {
            logger.error("Failed to retrieve chat messages");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GetChatMessagesResponse(false, "Failed to retrieve chat messages", List.of()));

        }
    }
}
