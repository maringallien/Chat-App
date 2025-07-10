package com.MarinGallien.JavaChatApp.java_chat_app.API.Controllers;

import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ContactRequests.CreateOrRemoveContactRequest;
import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Requests.ContactRequests.GetUserContactsRequest;
import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.ContactResponses.GetUserContactsResponse;
import com.MarinGallien.JavaChatApp.java_chat_app.API.DTOs.HTTPMessages.Responses.GenericResponse;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.Contact;
import com.MarinGallien.JavaChatApp.java_chat_app.Services.ContactService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private static Logger logger = LoggerFactory.getLogger(ContactController.class);
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createContact(
            @Valid @RequestBody CreateOrRemoveContactRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has any errors
            if (bindingResult.hasErrors()) {
                logger.warn("Failed to create contact: input parameter(s) null or empty");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Invalid Request parameters"));
            }

            // Delegate to ContactService
            Contact createdContact = contactService.createContact(request.userId(), request.contactId());

            // if createdContact is null, operation failed
            if (createdContact == null) {
                logger.error("Failed to create new contact");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Failed to create contact"));
            }

            // Send success message
            logger.info("Successfully created contact");
            return ResponseEntity.ok().body(new GenericResponse(true, "Successfully created contact"));

        } catch (Exception e) {
            logger.error("Failed to create contact: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal Server Error"));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<GenericResponse> removeContact (
            @Valid @RequestBody CreateOrRemoveContactRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has any errors
            if (bindingResult.hasErrors()) {
                logger.warn("Failed to delete contact: input parameter(s) null or empty");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Invalid Request parameters"));
            }

            // Delegate to ContactService
            boolean removed = contactService.removeContact(request.userId(), request.contactId());

            // Check operation success
            if (!removed) {
                logger.warn("Failed to remove contact");
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Failed to remove contact"));
            }

            // Send success response message
            logger.info("Successfully removed contact");
            return ResponseEntity.ok().body(new GenericResponse(true, "Successfully removed contact"));

        } catch (Exception e) {
            logger.error("Failed to delete contact: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal Server Error"));
        }
    }

    @GetMapping("/contacts")
    public ResponseEntity<GetUserContactsResponse> getUserContacts(
            @Valid @RequestBody GetUserContactsRequest request,
            BindingResult bindingResult) {

        try {
            // Return if input has any errors
            if (bindingResult.hasErrors()) {
                logger.warn("Failed to retrieve contacts list: input parameter(s) null or empty");
                return ResponseEntity.badRequest()
                        .body(new GetUserContactsResponse(false, "Invalid input parameters", List.of()));

            }

            // Delegate to ContactService
            List<User> contacts = contactService.getUserContacts(request.userId());

            // If contacts is null, operation failed
            if (contacts == null) {
                logger.warn("Failed to retrieve contacts list");
                return ResponseEntity.badRequest()
                        .body(new GetUserContactsResponse(false, "Failed to retrieve contacts list", List.of()));
            }

            // Send success response message
            logger.info("Successfully retrieved contacts list");
            return ResponseEntity.ok()
                    .body(new GetUserContactsResponse(true, "Successfully retrieved contacts list", contacts));
        } catch (Exception e) {
            logger.error("Failed to delete contact: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GetUserContactsResponse(false, "Internal Server Error", List.of()));
        }
    }

}
