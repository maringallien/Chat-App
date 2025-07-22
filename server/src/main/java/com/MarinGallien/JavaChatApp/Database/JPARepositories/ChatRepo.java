package com.MarinGallien.JavaChatApp.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.CoreEntities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepo extends JpaRepository<Chat, String> {
    // Returns a list of all chat IDs
    @Query("SELECT c.chatId FROM Chat c")
    List<String> findAllChatIds();

    // Delete a chat by its ID
    int deleteByChatId(@Param("chatId") String chatId);

    // Find a chat by ID
    @Query("SELECT c FROM Chat c WHERE c.chatId = :chatId")
    Chat findChatById(@Param("chatId") String chatId);
}
