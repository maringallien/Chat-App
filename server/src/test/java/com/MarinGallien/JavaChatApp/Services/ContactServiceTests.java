package com.MarinGallien.JavaChatApp.Services;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.UserDTO;
import com.MarinGallien.JavaChatApp.Database.DatabaseServices.ContactDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.JunctionEntities.Contact;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import com.MarinGallien.JavaChatApp.Mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTests {

    @Mock
    private ContactDbService contactDbService;

    @InjectMocks
    private ContactService contactService;

    @Mock
    private UserMapper userMapper;

    private Contact testContact;
    private User testUser1;
    private User testUser2;
    private User testUser3;
    private UserDTO testUserDTO2;
    private UserDTO testUserDTO3;
    private String user1Id = "user1";
    private String user2Id = "user2";
    private String user3Id = "user3";

    @BeforeEach
    void setUp() {
        testContact = new Contact();

        // Create test users with proper initialization
        testUser1 = new User("alice", "alice@test.com", "password1");
        testUser2 = new User("bob", "bob@test.com", "password2");
        testUser3 = new User("charlie", "charlie@test.com", "password3");

        // Create test DTOs
        testUserDTO2 = new UserDTO(
                user2Id,
                "bob",
                "bob@test.com",
                OnlineStatus.OFFLINE,
                LocalDateTime.now()
        );

        testUserDTO3 = new UserDTO(
                user3Id,
                "charlie",
                "charlie@test.com",
                OnlineStatus.OFFLINE,
                LocalDateTime.now()
        );
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
    void getUserContacts_ValidInputs_ReturnsContactsDTOsList() {
        // Given
        List<User> contactUsers = List.of(testUser2, testUser3);
        List<UserDTO> contactDTOs = List.of(testUserDTO2, testUserDTO3);

        when(contactDbService.getUserContacts(user1Id)).thenReturn(contactUsers);
        when(userMapper.toDTOList(contactUsers)).thenReturn(contactDTOs);

        // When
        List<UserDTO> result = contactService.getUserContactsDTOs(user1Id);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUserDTO2, result.get(0));
        assertEquals(testUserDTO3, result.get(1));

        verify(contactDbService).getUserContacts(user1Id);
        verify(userMapper).toDTOList(contactUsers);
    }

    @Test
    void getUserContacts_DTOs_InvalidParameters_ReturnsEmptyList() {
        // Test all null parameters
        assertTrue(contactService.getUserContactsDTOs(null).isEmpty());

        // Test all empty parameters
        assertTrue(contactService.getUserContactsDTOs("").isEmpty());

        // Test all whitespace parameters
        assertTrue(contactService.getUserContactsDTOs("   ").isEmpty());

        // Verify no database calls were made
        verify(contactDbService, never()).getUserContacts(any());
    }

    @Test
    void getUserContacts_DTOs_DatabaseReturnsEmpty_ReturnsEmptyList() {
        // Given
        when(contactDbService.getUserContacts(user1Id)).thenReturn(List.of());

        // When
        List<UserDTO> result = contactService.getUserContactsDTOs(user1Id);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactDbService).getUserContacts(user1Id);
    }
}