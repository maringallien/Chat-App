package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends JpaRepository<File, String> {
}
