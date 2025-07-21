package com.MarinGallien.JavaChatApp.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.CoreEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.JunctionEntities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
    // Retrieves the user IDs of a user's contacts
    @Query("SELECT c.contactUser FROM Contact c WHERE c.user.userId = :userId")
    List<User> findContactUserIdsByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(c) > 0 FROM Contact c WHERE " +
            "(c.user.userId = :userId1 AND c.contactUser.userId = :userId2) OR " +
            "(c.user.userId = :userId2 AND c.contactUser.userId = :userId1)")
    boolean areUsersContacts(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Query("SELECT c FROM Contact c WHERE c.user.userId = :usedId AND c.contactUser.userId = :contactUsedId")
    Contact findContactById(@Param("usedId") String usedId, @Param("contactUsedId") String contactUsedId);
}
