package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPARepositories;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.Chat;
import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities.ChatParticipant;
import jakarta.transaction.Transactional;
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
    @Transactional
    int removeByChatChatIdAndUserUserId(@Param("chatId") String chatId, @Param("userId") String userId);

    // Check if a user is in a room
    boolean existsByChatChatIdAndUserUserId(String chatId, String userId);

    // FIXED: Added proper @Query annotation
    @Query("SELECT cp.chat FROM ChatParticipant cp WHERE cp.user.userId = :userId")
    List<Chat> findChatsByUserUserId(@Param("userId") String userId);
}
