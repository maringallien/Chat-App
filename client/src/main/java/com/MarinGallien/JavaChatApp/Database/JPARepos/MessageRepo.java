package com.MarinGallien.JavaChatApp.Database.JPARepos;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, String> {

    @Query("SELECT m FROM Message m WHERE m.chat.chatId = :chatId")
    List<Message> findByChatChatId(@Param("chatId") String chatId);

    void deleteByChatId(String chatId);
}