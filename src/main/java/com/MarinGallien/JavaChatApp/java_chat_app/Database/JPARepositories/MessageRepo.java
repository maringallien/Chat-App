package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepo extends JpaRepository<Message, String> {

    // Add this method to find messages by chat ID, ordered chronologically
    @Query("SELECT m FROM Message m WHERE m.chat.chatId = :chatId ORDER BY m.sentAt ASC")
    List<Message> findByChatChatIdOrderBySentAtAsc(@Param("chatId") String chatId);
}
