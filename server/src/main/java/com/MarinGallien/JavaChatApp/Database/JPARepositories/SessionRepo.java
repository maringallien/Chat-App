package com.MarinGallien.JavaChatApp.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.CoreEntities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepo extends JpaRepository<Session, String> {
}
