package com.MarinGallien.JavaChatApp.Database.Mappers;

import com.MarinGallien.JavaChatApp.DTOs.DataEntities.FileDTO;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.File;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileMapper {

    public FileDTO toDTO(File file) {
        if (file == null) return null;

        return new FileDTO(
                file.getFileId(),
                file.getFilename(),
                file.getFileSize(),
                file.getFileType(),
                file.getMessage().getChat().getChatId()
        );
    }

    public List<FileDTO> toDTOList(List<File> files) {
        return files.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}