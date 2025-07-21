package Mappers;

import DTOs.DataEntities.FileDTO;
import Database.JPAEntities.CoreEntities.File;
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
                file.getSentAt(),
                file.getUploader().getUserId(),
                file.getUploader().getUsername(),
                file.getMessage().getChat().getChatId()
        );
    }

    public List<FileDTO> toDTOList(List<File> files) {
        return files.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}