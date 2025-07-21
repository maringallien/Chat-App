package Database;

import Database.DatabaseServices.SessionDbService;
import Database.JPAEntities.CoreEntities.User;
import Database.JPARepositories.UserRepo;
import Enums.OnlineStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(SessionDbService.class)
public class SessionDbServiceTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessionDbService sessionDbService;

    @Autowired
    private UserRepo userRepo;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User("alice", "alice@test.com", "password1");
        user2 = new User("bob", "bob@test.com", "password2");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
    }

    // ==========================================================================
    // UPDATE STATUS TESTS
    // ==========================================================================

    @Test
    void updateStatus_ValidInputs_UpdatesStatus() {
        // Given - user starts with OFFLINE status (default)
        assertEquals(OnlineStatus.OFFLINE, user1.getStatus());

        // When
        OnlineStatus updatedStatus = sessionDbService.updateStatus(user1.getUserId(), OnlineStatus.ONLINE);

        // Then
        assertEquals(OnlineStatus.ONLINE, updatedStatus);

        // Verify status was persisted in database
        User refreshedUser = userRepo.findUserById(user1.getUserId());
        assertEquals(OnlineStatus.ONLINE, refreshedUser.getStatus());
    }

    @Test
    void updateStatus_NonExistentUser_ReturnsNull() {
        // When
        OnlineStatus updatedStatus = sessionDbService.updateStatus("fake_user_id", OnlineStatus.ONLINE);

        // Then
        assertNull(updatedStatus);
    }

    @Test
    void updateStatus_OfflineToOnline_UpdatesCorrectly() {
        // Given - user starts OFFLINE
        assertEquals(OnlineStatus.OFFLINE, user1.getStatus());

        // When
        OnlineStatus updatedStatus = sessionDbService.updateStatus(user1.getUserId(), OnlineStatus.ONLINE);

        // Then
        assertEquals(OnlineStatus.ONLINE, updatedStatus);
        assertEquals(OnlineStatus.ONLINE, userRepo.findUserById(user1.getUserId()).getStatus());
    }

    @Test
    void updateStatus_OnlineToOffline_UpdatesCorrectly() {
        // Given - set user to ONLINE first
        sessionDbService.updateStatus(user1.getUserId(), OnlineStatus.ONLINE);
        assertEquals(OnlineStatus.ONLINE, userRepo.findUserById(user1.getUserId()).getStatus());

        // When
        OnlineStatus updatedStatus = sessionDbService.updateStatus(user1.getUserId(), OnlineStatus.OFFLINE);

        // Then
        assertEquals(OnlineStatus.OFFLINE, updatedStatus);
        assertEquals(OnlineStatus.OFFLINE, userRepo.findUserById(user1.getUserId()).getStatus());
    }

    @Test
    void updateStatus_SameStatus_StillWorks() {
        // Given - user is already OFFLINE
        assertEquals(OnlineStatus.OFFLINE, user1.getStatus());

        // When - set to OFFLINE again
        OnlineStatus updatedStatus = sessionDbService.updateStatus(user1.getUserId(), OnlineStatus.OFFLINE);

        // Then
        assertEquals(OnlineStatus.OFFLINE, updatedStatus);
        assertEquals(OnlineStatus.OFFLINE, userRepo.findUserById(user1.getUserId()).getStatus());
    }

    @Test
    void updateStatus_MultipleUsers_UpdatesIndependently() {
        // Given - both users start OFFLINE
        assertEquals(OnlineStatus.OFFLINE, user1.getStatus());
        assertEquals(OnlineStatus.OFFLINE, user2.getStatus());

        // When - update different users to different statuses
        OnlineStatus user1Status = sessionDbService.updateStatus(user1.getUserId(), OnlineStatus.ONLINE);
        OnlineStatus user2Status = sessionDbService.updateStatus(user2.getUserId(), OnlineStatus.OFFLINE);

        // Then
        assertEquals(OnlineStatus.ONLINE, user1Status);
        assertEquals(OnlineStatus.OFFLINE, user2Status);

        // Verify in database
        assertEquals(OnlineStatus.ONLINE, userRepo.findUserById(user1.getUserId()).getStatus());
        assertEquals(OnlineStatus.OFFLINE, userRepo.findUserById(user2.getUserId()).getStatus());
    }
}