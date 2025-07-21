package com.MarinGallien.JavaChatApp.Database;

import com.MarinGallien.JavaChatApp.Database.DatabaseServices.UserDbService;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.UserRepo;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({UserDbService.class, BCryptPasswordEncoder.class})
public class UserDbServiceTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserDbService userDbService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user1;

    @BeforeEach
    void setUp() {
        user1 = new User("alice", "alice@test.com", passwordEncoder.encode("password123"));
        entityManager.persistAndFlush(user1);
    }

    // ==========================================================================
    // CREATE USER TESTS
    // ==========================================================================

    @Test
    void createUser_ValidInputs_CreatesUser() {
        // When
        User createdUser = userDbService.createUser("bob", "bob@test.com", "password456");

        // Then
        assertNotNull(createdUser);
        assertEquals("bob", createdUser.getUsername());
        assertEquals("bob@test.com", createdUser.getEmail());
        assertEquals(OnlineStatus.OFFLINE, createdUser.getStatus());
        assertTrue(passwordEncoder.matches("password456", createdUser.getPasswordHash()));
        assertTrue(userRepo.existsById(createdUser.getUserId()));
    }

    @Test
    void createUser_DuplicateEmail_ReturnsNull() {
        // When - try to create user with existing email
        User duplicateUser = userDbService.createUser("bob", "alice@test.com", "password456");

        // Then
        assertNull(duplicateUser);
    }

    @Test
    void createUser_DuplicateUsername_ReturnsNull() {
        // When - try to create user with existing username
        User duplicateUser = userDbService.createUser("alice", "bob@test.com", "password456");

        // Then
        assertNull(duplicateUser);
    }

    // ==========================================================================
    // LOGIN TESTS
    // ==========================================================================

    @Test
    void login_ValidCredentials_ReturnsTrue() {
        // When
        boolean loginResult = userDbService.login("alice@test.com", "password123");

        // Then
        assertTrue(loginResult);
    }

    @Test
    void login_InvalidEmail_ReturnsFalse() {
        // When
        boolean loginResult = userDbService.login("fake@test.com", "password123");

        // Then
        assertFalse(loginResult);
    }

    @Test
    void login_InvalidPassword_ReturnsFalse() {
        // When
        boolean loginResult = userDbService.login("alice@test.com", "wrongpassword");

        // Then
        assertFalse(loginResult);
    }

    // ==========================================================================
    // DELETE USER TESTS
    // ==========================================================================

    @Test
    void deleteUser_ValidCredentials_DeletesUser() {
        // When
        boolean deleted = userDbService.deleteUser(user1.getUserId(), "password123");

        // Then
        assertTrue(deleted);
        assertFalse(userRepo.existsById(user1.getUserId()));
    }

    @Test
    void deleteUser_NonExistentUser_ReturnsFalse() {
        // When
        boolean deleted = userDbService.deleteUser("fake_user_id", "password123");

        // Then
        assertFalse(deleted);
    }

    @Test
    void deleteUser_WrongPassword_ReturnsFalse() {
        // When
        boolean deleted = userDbService.deleteUser(user1.getUserId(), "wrongpassword");

        // Then
        assertFalse(deleted);
        assertTrue(userRepo.existsById(user1.getUserId()));
    }

    // ==========================================================================
    // UPDATE USERNAME TESTS
    // ==========================================================================

    @Test
    void updateUsername_ValidInputs_UpdatesUsername() {
        // When
        String updatedUsername = userDbService.updateUsername(user1.getUserId(), "alice_updated");

        // Then
        assertEquals("alice_updated", updatedUsername);
        assertEquals("alice_updated", userRepo.findUserById(user1.getUserId()).getUsername());
    }

    @Test
    void updateUsername_NonExistentUser_ReturnsNull() {
        // When
        String updatedUsername = userDbService.updateUsername("fake_user_id", "new_username");

        // Then
        assertNull(updatedUsername);
    }

    @Test
    void updateUsername_SameUsername_ReturnsNull() {
        // When - try to update to same username
        String updatedUsername = userDbService.updateUsername(user1.getUserId(), "alice");

        // Then
        assertNull(updatedUsername);
    }

    // ==========================================================================
    // UPDATE EMAIL TESTS
    // ==========================================================================

    @Test
    void updateEmail_ValidInputs_UpdatesEmail() {
        // When
        String updatedEmail = userDbService.updateEmail(user1.getUserId(), "alice_new@test.com");

        // Then
        assertEquals("alice_new@test.com", updatedEmail);
        assertEquals("alice_new@test.com", userRepo.findUserById(user1.getUserId()).getEmail());
    }

    @Test
    void updateEmail_NonExistentUser_ReturnsNull() {
        // When
        String updatedEmail = userDbService.updateEmail("fake_user_id", "new@test.com");

        // Then
        assertNull(updatedEmail);
    }

    @Test
    void updateEmail_SameEmail_ReturnsNull() {
        // When - try to update to same email
        String updatedEmail = userDbService.updateEmail(user1.getUserId(), "alice@test.com");

        // Then
        assertNull(updatedEmail);
    }

    // ==========================================================================
    // UPDATE PASSWORD TESTS
    // ==========================================================================

    @Test
    void updatePassword_ValidInputs_UpdatesPassword() {
        // When
        boolean updated = userDbService.updatePassword(user1.getUserId(), "password123", "newpassword456");

        // Then
        assertTrue(updated);

        // Verify new password works
        User updatedUser = userRepo.findUserById(user1.getUserId());
        assertTrue(passwordEncoder.matches("newpassword456", updatedUser.getPasswordHash()));

        // Verify old password no longer works
        assertFalse(passwordEncoder.matches("password123", updatedUser.getPasswordHash()));
    }

    @Test
    void updatePassword_NonExistentUser_ReturnsFalse() {
        // When
        boolean updated = userDbService.updatePassword("fake_user_id", "password123", "newpassword");

        // Then
        assertFalse(updated);
    }

    @Test
    void updatePassword_WrongOldPassword_ReturnsFalse() {
        // When
        boolean updated = userDbService.updatePassword(user1.getUserId(), "wrongpassword", "newpassword456");

        // Then
        assertFalse(updated);

        // Verify original password still works
        assertTrue(passwordEncoder.matches("password123", user1.getPasswordHash()));
    }

    // ==========================================================================
    // UPDATE STATUS TESTS
    // ==========================================================================

    @Test
    void updateStatus_ValidInputs_UpdatesStatus() {
        // When
        OnlineStatus updatedStatus = userDbService.updateStatus(user1.getUserId(), OnlineStatus.ONLINE);

        // Then
        assertEquals(OnlineStatus.ONLINE, updatedStatus);
        assertEquals(OnlineStatus.ONLINE, userRepo.findUserById(user1.getUserId()).getStatus());
    }

    @Test
    void updateStatus_NonExistentUser_ReturnsNull() {
        // When
        OnlineStatus updatedStatus = userDbService.updateStatus("fake_user_id", OnlineStatus.ONLINE);

        // Then
        assertNull(updatedStatus);
    }

    // ==========================================================================
    // VALIDATE CREDENTIALS TESTS
    // ==========================================================================

    @Test
    void validateCredentials_ValidCredentials_ReturnsTrue() {
        // When
        boolean valid = userDbService.validateCredentials("alice@test.com", "password123");

        // Then
        assertTrue(valid);
    }

    @Test
    void validateCredentials_InvalidEmail_ReturnsFalse() {
        // When
        boolean valid = userDbService.validateCredentials("fake@test.com", "password123");

        // Then
        assertFalse(valid);
    }

    @Test
    void validateCredentials_InvalidPassword_ReturnsFalse() {
        // When
        boolean valid = userDbService.validateCredentials("alice@test.com", "wrongpassword");

        // Then
        assertFalse(valid);
    }
}