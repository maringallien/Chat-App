package com.MarinGallien.JavaChatApp.Database.Mappers;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.MessageDTO;
import com.MarinGallien.JavaChatApp.JPAEntities.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {
    public MessageDTO toDTO(Message message) {
        return new MessageDTO(
                message.getMessageId(),
                message.getSender().getUserId(),
                message.getChat().getChatId(),
                message.getContent(),
                message.getSentAt()
        );
    }

    public List<MessageDTO> toDTOList(List<Message> messages) {
        return messages.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}