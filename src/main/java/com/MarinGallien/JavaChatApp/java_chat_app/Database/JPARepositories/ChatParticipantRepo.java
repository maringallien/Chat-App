package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatParticipantRepo extends JpaRepository<ChatParticipant, String> {
    // Retrieve all chat-user mappings in a single query in a List of objects array where [0] = chatId and [1] = userId
    @Query("SELECT cp.chat.chatId, cp.user.userId FROM ChatParticipant cp")
    List<Object[]> findAllChatUserMappings();

    // Remove a participant from a chat
    @Modifying
    @Query("DELETE FROM ChatParticipant cp WHERE cp.chat.chatId == :chatId AND cp.user.userId = :userId")
    int removeByChatIdAndUserId(@Param("chatId") String chatId, @Param("userId") String userId);

    // Check if a user is in a room
    boolean existsByChatIdAndUserId(String chatId, String userId);
}
