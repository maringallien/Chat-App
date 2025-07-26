package com.MarinGallien.JavaChatApp.Database;

import com.MarinGallien.JavaChatApp.Database.DatabaseServices.ContactDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.Contact;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.ContactRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(ContactDbService.class)
public class ContactDbServiceTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContactDbService contactDbService;

    @Autowired
    private ContactRepo contactRepo;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        // Initialize test users
        user1 = new User("alice", "alice@test.com", "password1");
        user2 = new User("bob", "bob@test.com", "password2");
        user3 = new User("charlie", "charlie@test.com", "password3");

        // Persist them in database
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
    }

    // ==========================================================================
    // CREATE CONTACT TESTS
    // ==========================================================================

    @Test
    void createContact_ValidUsers_CreatesContact() {
        // When
        Contact contact = contactDbService.createContact(user1.getUserId(), user2.getUserId());

        // Then
        assertNotNull(contact);
        assertTrue(contactRepo.areUsersContacts(user1.getUserId(), user2.getUserId()));
    }

    @Test
    void createContact_NonExistentUser_ReturnsNull() {
        // When
        Contact contact = contactDbService.createContact("fake_id", user2.getUserId());

        // Then
        assertNull(contact);
    }

    @Test
    void createContact_AlreadyContacts_ReturnsNull() {
        // Given
        Contact initialContact = contactDbService.createContact(user1.getUserId(), user2.getUserId());
        assertNotNull(initialContact);

        // When
        Contact duplicateContact = contactDbService.createContact(user1.getUserId(), user2.getUserId());

        // Then
        assertNull(duplicateContact);
    }

    // ==========================================================================
    // REMOVE CONTACT TESTS
    // ==========================================================================

    @Test
    void removeContact_ValidUsers_RemovesContact() {
        // Given
        Contact contact = contactDbService.createContact(user1.getUserId(), user2.getUserId());
        assertNotNull(contact);

        // When
        boolean removed = contactDbService.removeContact(user1.getUserId(), user2.getUserId());

        // Then
        assertTrue(removed);
        assertFalse(contactRepo.areUsersContacts(user1.getUserId(), user2.getUserId()));
    }

    @Test
    void removeContact_NonExistentUser_ReturnsFalse() {
        // When
        boolean removed = contactDbService.removeContact("fake_id", user2.getUserId());

        // Then
        assertFalse(removed);
    }

    @Test
    void removeContact_NotContacts_ReturnsFalse() {
        // When
        boolean removed = contactDbService.removeContact(user1.getUserId(), user2.getUserId());

        // Then
        assertFalse(removed);
    }

    // ==========================================================================
    // GET USER CONTACTS TESTS
    // ==========================================================================

    @Test
    void getUserContacts_ValidUser_ReturnsContactsList() {
        // Given
        contactDbService.createContact(user1.getUserId(), user2.getUserId());
        contactDbService.createContact(user1.getUserId(), user3.getUserId());

        // When
        List<User> contacts = contactDbService.getUserContacts(user1.getUserId());

        // Then
        assertNotNull(contacts);
        assertEquals(2, contacts.size());
        assertTrue(contacts.contains(user2));
        assertTrue(contacts.contains(user3));
    }

    @Test
    void getUserContacts_UserWithNoContacts_ReturnsEmptyList() {
        // When
        List<User> contacts = contactDbService.getUserContacts(user1.getUserId());

        // Then
        assertNotNull(contacts);
        assertTrue(contacts.isEmpty());
    }

    @Test
    void getUserContacts_NonExistentUser_ReturnsEmptyList() {
        // When
        List<User> contacts = contactDbService.getUserContacts("fake_user_id");

        // Then
        assertNotNull(contacts);
        assertTrue(contacts.isEmpty());
    }
}
