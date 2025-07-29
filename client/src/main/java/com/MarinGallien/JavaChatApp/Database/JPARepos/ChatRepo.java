package com.MarinGallien.JavaChatApp.Database.JPARepos;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepo extends JpaRepository<Chat, String> {

    @Query("SELECT c FROM Chat c WHERE c.chatId = :chatId")
    Optional<Chat> findByChatId(@Param("chatId") String chatId);

    @Query("SELECT c FROM Chat c WHERE :userId MEMBER OF c.participantIds")
    List<Chat> findChatsByUserId(@Param("userId") String userId);

    void deleteByChatId(String chatId);
}