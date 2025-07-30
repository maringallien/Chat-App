package com.MarinGallien.JavaChatApp.Database.Mappers;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.ContactDTO;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public ContactDTO toDTO(User user) {
        return new ContactDTO(
                user.getUserId(),
                user.getUsername(),
                user.getStatus(),
                user.getDateJoined()
        );
    }

    public List<ContactDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
