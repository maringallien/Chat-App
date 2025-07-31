package com.MarinGallien.JavaChatApp.Database.Mappers;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ChatDTO;
import com.MarinGallien.JavaChatApp.JPAEntities.Chat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatMapper {

    public ChatDTO toDTO(Chat chat) {
        if (chat == null) return null;

        // Extract participant IDs from ChatParticipant entities
        List<String> participantIds = chat.getParticipants().stream()
                .map(participant -> participant.getUser().getUserId())
                .collect(Collectors.toList());

        return new ChatDTO(
                chat.getChatId(),
                chat.getChatType(),
                chat.getChatName(),
                chat.getCreatorId(),
                participantIds,
                chat.getCreatedAt()
        );
    }

    public List<ChatDTO> toDTOList(List<Chat> chats) {
        return chats.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}