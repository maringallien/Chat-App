package com.MarinGallien.JavaChatApp.Database.JPARepos;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
    Contact findByUserId(String userId);

    @Query("SELECT c.userId FROM Contact WHERE c.username = :username")
    String findUserIdByUsername(@Param("username") String username);

    @Query("SELECT c FROM Contact c ORDER BY c.createdAt DESC LIMIT 1")
    Contact getLatestContact();
}
