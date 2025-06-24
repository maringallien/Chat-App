package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface FileRepo extends JpaRepository<File, String> {

    // Retrieves a list of files belonging to a chat
    List<File> findByMessageChatChatId(String chatId);
}
