package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
    // Retrieves the user IDs of a user's contacts
    @Query("SELECT c.contactUser.userId FROM Contact c WHERE c.user.userId = :userId")
    List<String> findContactUserIdsByUserId(@Param("userId") String userId);
}
