package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.ContactDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.Contact;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Transactional
public class ContactService{

    private static Logger logger = LoggerFactory.getLogger(ContactService.class);

    private final ContactDbService contactDbService;

    public ContactService(ContactDbService contactDbService) {
        this.contactDbService = contactDbService;
    }

    public Contact createContact(String userId, String contactUserId) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(contactUserId)) {
                logger.warn("Failed to create contact: invalid input parameters");
                return null;
            }

            if (userId.equals(contactUserId)) {
                logger.warn("Failed to create contact: user cannot add themselves as contact");
                return null;
            }

            // Call database layer
            Contact createdContact = contactDbService.createContact(userId, contactUserId);

            if (createdContact == null) {
                logger.warn("Failed to create contact between user {} and {}", userId, contactUserId);
                return null;
            }

            logger.info("Successfully created contact between user {} and {}", userId, contactUserId);
            return createdContact;

        } catch (Exception e) {
            logger.error("Error creating contact: {}", e.getMessage());
            return null;
        }
    }

    public boolean removeContact(String userId, String contactUserId) {
        try {
            // Validate inputs
            if (!validateId(userId) || !validateId(contactUserId)) {
                logger.warn("Failed to remove contact: invalid input parameters");
                return false;
            }

            if (userId.equals(contactUserId)) {
                logger.warn("Failed to remove contact: user cannot remove themselves as contact");
                return false;
            }

            // Call database layer
            boolean removed = contactDbService.removeContact(userId, contactUserId);

            if (!removed) {
                logger.warn("Failed to remove contact between user {} and {}", userId, contactUserId);
                return false;
            }

            logger.info("Successfully removed contact between user {} and {}", userId, contactUserId);
            return true;

        } catch (Exception e) {
            logger.error("Error removing contact: {}", e.getMessage());
            return false;
        }
    }

    public List<String> getUserContacts(String userId) {
        try {
            if (!validateId(userId)) {
                logger.warn("Failed to retrieve contacts list: used ID is null or empty");
                return List.of();
            }

            return contactDbService.getUserContacts(userId);

        } catch (Exception e) {
            logger.error("Failed to retrieve contacts list");
            return List.of();
        }
    }

    private boolean validateId(String id) {
        return id != null && !id.trim().isEmpty();
    }

}
