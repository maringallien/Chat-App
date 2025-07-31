package com.MarinGallien.JavaChatApp.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, String> {

    // Add this method to find messages by chat ID, ordered chronologically
    @Query("SELECT m FROM Message m WHERE m.chat.chatId = :chatId ORDER BY m.sentAt ASC")
    List<Message> findByChatChatIdOrderBySentAtAsc(@Param("chatId") String chatId);
}
