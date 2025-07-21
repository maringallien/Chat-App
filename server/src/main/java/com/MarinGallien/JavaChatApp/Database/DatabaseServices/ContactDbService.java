package com.MarinGallien.JavaChatApp.Database.DatabaseServices;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.JunctionEntities.Contact;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.ContactRepo;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.UserRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ContactDbService {

    private static Logger logger = LoggerFactory.getLogger(ContactDbService.class);

    private final UserRepo userRepo;
    private final ContactRepo contactRepo;

    public ContactDbService(UserRepo userRepo, ContactRepo contactRepo) {
        this.userRepo = userRepo;
        this.contactRepo = contactRepo;
    }

    public Contact createContact(String userId, String contactUserId) {
        try {
            // Validate inputs
            if (!userRepo.existsById(userId) || !userRepo.existsById(contactUserId)) {
                logger.warn("Failed to create contact: one of the user IDs does not exist");
                return null;
            }

            if(contactRepo.areUsersContacts(userId, contactUserId)) {
                logger.warn("Failed to create new contact: user {} and {} are already contacts.", userId, contactUserId);
                return null;
            }

            // Retrieve users
            User user = userRepo.findUserById(userId);
            User contactUser = userRepo.findUserById(contactUserId);

            // Create new contacts
            Contact contact1 = new Contact(user, contactUser);
            Contact contact2 = new Contact(contactUser, user);

            // Save contacts
            Contact savedContact1 = contactRepo.save(contact1);
            Contact savedContact2 = contactRepo.save(contact2);

            // Return savedContact1 because operation was requested by userId, not contactUserId
            logger.info("Successfully created new contact between user {} and {}", userId, contactUserId);
            return savedContact1;

        } catch (Exception e) {
            logger.error("Failed to add user {} as contact of user {}: {}", userId, contactUserId, e.getMessage());
            return null;
        }
    }

    public boolean removeContact(String userId, String contactUserId) {
        try {
            // Validate inputs
            if (!userRepo.existsById(userId) || !userRepo.existsById(contactUserId)) {
                logger.warn("Failed to delete contact: one of the user IDs does not exist");
                return false;
            }

            if(!contactRepo.areUsersContacts(userId, contactUserId)) {
                logger.warn("Failed to delete contact: user {} and {} are not contacts.", userId, contactUserId);
                return false;
            }

            // Retrieve the contacts
            Contact contact1 = contactRepo.findContactById(userId, contactUserId);
            Contact contact2 = contactRepo.findContactById(contactUserId, userId);

            // Make sure contacts are not null before deleting
            if (contact1 == null || contact2 == null) {
                logger.warn("Failed to delete contact: one of the contacts is null");
                return false;
            }

            // Delete the contacts
            contactRepo.delete(contact1);
            contactRepo.delete(contact2);

            logger.info("Successfully remove contact between used {} and {}", userId, contactUserId);
            return true;

        } catch (Exception e) {
            logger.error("Failed to delete user {} as contact of user {}: {}", userId, contactUserId, e.getMessage());
            return false;
        }
    }

    public List<User> getUserContacts(String userId) {
        try {
            if (!userRepo.existsById(userId)) {
                logger.warn("Failed to retrieve contacts list: used ID does not exist");
                return List.of();
            }

            List<User> contacts = contactRepo.findContactUserIdsByUserId(userId);

            // If operation fails, return empty list
            if (contacts == null) {
                logger.warn("Failed to retrieve contacts for user {}", userId);
                return List.of();
            }

            return contacts;

        } catch (Exception e) {
            logger.error("Failed to retrieve contacts list: {}", e.getMessage());
            return List.of();
        }
    }
}
