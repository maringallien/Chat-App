package com.MarinGallien.JavaChatApp.Database.JPARepos;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, String> {

    @Query("SELECT m FROM Message m WHERE m.chatId = :chatId ORDER BY m.messageId ASC")
    List<Message> findByChatIdOrderByMessageIdAsc(@Param("chatId") String chatId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatId = :chatId")
    long countByChatId(@Param("chatId") String chatId);

    void deleteByChatId(String chatId);
}