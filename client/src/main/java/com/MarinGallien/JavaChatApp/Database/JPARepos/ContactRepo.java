package com.MarinGallien.JavaChatApp.Database.JPARepos;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
    Contact findByUserId(String userId);
}