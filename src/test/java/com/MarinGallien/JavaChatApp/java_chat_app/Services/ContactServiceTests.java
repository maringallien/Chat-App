package com.MarinGallien.JavaChatApp.java_chat_app.Services;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.DatabaseServices.ContactDbService;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTests {

    @Mock
    private ContactDbService contactDbService;

    @InjectMocks
    private ContactService contactService;

    private Contact testContact;
    private User testUser1;
    private User testUser2;
    private User testUser3;
    private String user1Id = "user1";
    private String user2Id = "user2";
    private String user3Id = "user3";

    @BeforeEach
    void setUp() {
        testContact = new Contact();
        // Note: Contact entity doesn't have public setters for users in the provided code,
        // so we'll work with the mock behavior
    }

    // ==========================================================================
    // CREATE CONTACT TESTS
    // ==========================================================================

    @Test
    void createContact_ValidInputs_CreatesContact() {
        // Given
        when(contactDbService.createContact(user1Id, user2Id)).thenReturn(testContact);

        // When
        Contact result = contactService.createContact(user1Id, user2Id);

        // Then
        assertNotNull(result);
        assertEquals(testContact, result);
        verify(contactDbService).createContact(user1Id, user2Id);
    }

    @Test
    void createContact_InvalidParameters_ReturnsNull() {
        // Test all null parameters
        assertNull(contactService.createContact(null, user2Id));
        assertNull(contactService.createContact(user1Id, null));

        // Test all empty parameters
        assertNull(contactService.createContact("", user2Id));
        assertNull(contactService.createContact(user1Id, ""));

        // Test all whitespace parameters
        assertNull(contactService.createContact("   ", user2Id));
        assertNull(contactService.createContact(user1Id, "   "));

        // Test same user IDs (business rule)
        assertNull(contactService.createContact(user1Id, user1Id));

        // Verify no database calls were made
        verify(contactDbService, never()).createContact(any(), any());
    }

    @Test
    void createContact_DatabaseFailure_ReturnsNull() {
        // Given
        when(contactDbService.createContact(user1Id, user2Id)).thenReturn(null);

        // When
        Contact result = contactService.createContact(user1Id, user2Id);

        // Then
        assertNull(result);
        verify(contactDbService).createContact(user1Id, user2Id);
    }

    // ==========================================================================
    // REMOVE CONTACT TESTS
    // ==========================================================================

    @Test
    void removeContact_ValidInputs_RemovesContact() {
        // Given
        when(contactDbService.removeContact(user1Id, user2Id)).thenReturn(true);

        // When
        boolean result = contactService.removeContact(user1Id, user2Id);

        // Then
        assertTrue(result);
        verify(contactDbService).removeContact(user1Id, user2Id);
    }

    @Test
    void removeContact_InvalidParameters_ReturnsFalse() {
        // Test all null parameters
        assertFalse(contactService.removeContact(null, user2Id));
        assertFalse(contactService.removeContact(user1Id, null));

        // Test all empty parameters
        assertFalse(contactService.removeContact("", user2Id));
        assertFalse(contactService.removeContact(user1Id, ""));

        // Test all whitespace parameters
        assertFalse(contactService.removeContact("   ", user2Id));
        assertFalse(contactService.removeContact(user1Id, "   "));

        // Test same user IDs (business rule)
        assertFalse(contactService.removeContact(user1Id, user1Id));

        // Verify no database calls were made
        verify(contactDbService, never()).removeContact(any(), any());
    }

    @Test
    void removeContact_DatabaseFailure_ReturnsFalse() {
        // Given
        when(contactDbService.removeContact(user1Id, user2Id)).thenReturn(false);

        // When
        boolean result = contactService.removeContact(user1Id, user2Id);

        // Then
        assertFalse(result);
        verify(contactDbService).removeContact(user1Id, user2Id);
    }

    // ==========================================================================
    // GET USER CONTACTS TESTS
    // ==========================================================================

    @Test
    void getUserContacts_ValidInputs_ReturnsContactsList() {
        // Given
        List<User> contactUsers = List.of(testUser2, testUser3);
        when(contactDbService.getUserContacts(user1Id)).thenReturn(contactUsers);

        // When
        List<User> result = contactService.getUserContacts(user1Id);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testUser2));
        assertTrue(result.contains(testUser3));
        verify(contactDbService).getUserContacts(user1Id);
    }

    @Test
    void getUserContacts_InvalidParameters_ReturnsEmptyList() {
        // Test all null parameters
        assertTrue(contactService.getUserContacts(null).isEmpty());

        // Test all empty parameters
        assertTrue(contactService.getUserContacts("").isEmpty());

        // Test all whitespace parameters
        assertTrue(contactService.getUserContacts("   ").isEmpty());

        // Verify no database calls were made
        verify(contactDbService, never()).getUserContacts(any());
    }

    @Test
    void getUserContacts_DatabaseReturnsEmpty_ReturnsEmptyList() {
        // Given
        when(contactDbService.getUserContacts(user1Id)).thenReturn(List.of());

        // When
        List<User> result = contactService.getUserContacts(user1Id);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactDbService).getUserContacts(user1Id);
    }
}