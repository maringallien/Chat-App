package com.MarinGallien.JavaChatApp.Database.Mappers;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.UserDTO;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getStatus()
        );
    }

    public List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
