package com.MarinGallien.JavaChatApp.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepo extends JpaRepository<File, String> {

    // Retrieves a list of files belonging to a chat
    List<File> findByMessageChatChatId(@Param("chatId") String chatId);

    @Query("SELECT f FROM File f WHERE f.filename = :filename")
    File findFileByFilename(@Param("filename") String filename);
}
