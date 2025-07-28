package com.MarinGallien.JavaChatApp.Database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
    Contact findByUserId(String userId);
}