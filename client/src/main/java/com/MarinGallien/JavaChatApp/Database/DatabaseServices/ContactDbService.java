package com.MarinGallien.JavaChatApp.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Contact;
import com.MarinGallien.JavaChatApp.Database.JPARepos.ContactRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class ContactDbService {

    private static final Logger logger = LoggerFactory.getLogger(ContactDbService.class);

    private static ContactRepo contactRepo;

    public String findContact(String username) {
        try {
            return contactRepo.findUserIdByUsername(username);
        } catch (Exception e) {
            logger.error("Error retrieving contact: {}", e.getMessage(), e);
            return null;
        }
    }

    public List<Contact> getContacts() {
        try {
            return contactRepo.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving contacts: {}", e.getMessage(), e);
            return null;
        }
    }

    public void updateContactStatus(String userId, com.MarinGallien.JavaChatApp.Enums.OnlineStatus status) {
        try {
            Contact contact = contactRepo.findByUserId(userId);
            if (contact != null) {
                contact.setStatus(status);
                contactRepo.save(contact);
                logger.debug("Updated contact {} status to {}", userId, status);
            }
        } catch (Exception e) {
            logger.error("Error updating contact status: {}", e.getMessage(), e);
        }
    }

    public LocalDateTime getLastContactTimestamp() {
        try {
            Contact lastContact = contactRepo.getLatestContact();
            return lastContact != null ? lastContact.getCreatedAt() : null;
        } catch (Exception e) {
            logger.error("Error retrieving last contact timestamp: {}", e.getMessage(), e);
            return null;
        }
    }

    public void addNewContacts(List<ContactDTO> newContacts) {
        try {
            logger.info("Adding {} new contacts to local database", newContacts.size());

            for (ContactDTO contactDTO : newContacts) {
                // Check if contact already exists to avoid duplicates
                Contact existingContact = contactRepo.findByUserId(contactDTO.getUserId());
                if (existingContact == null) {
                    Contact contact = new Contact(
                            contactDTO.getUserId(),
                            contactDTO.getUsername(),
                            contactDTO.getOnlineStatus(),
                            contactDTO.getCreatedAt()
                    );
                    contactRepo.save(contact);
                }
            }

            logger.info("Successfully added {} new contacts to local database", newContacts.size());
        } catch (Exception e) {
            logger.error("Error adding new contacts to local database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add new contacts to local database", e);
        }
    }

}
