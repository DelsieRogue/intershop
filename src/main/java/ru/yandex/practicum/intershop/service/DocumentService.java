package ru.yandex.practicum.intershop.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DocumentService {
    private final String serverPath;

    public DocumentService(@Value("${image.folder.server.path}") String serverPath) {
        this.serverPath = serverPath;
    }

    public Optional<String> save(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            log.warn("Файл отсутствует/пустой, загрузка не выполнена.");
            return Optional.empty();
        }
        try {
            String extension = FilenameUtils.getExtension(image.getOriginalFilename());
            String fileName = "file_" + UUID.randomUUID() + "." + extension;
            File destinationFile = new File(serverPath, fileName);
            image.transferTo(destinationFile);
            return Optional.of(fileName);
        } catch (IOException e) {
            log.error("Ошибка сохранения файла", e);
            throw new IllegalArgumentException("Ошибка сохранения файла");
        }
    }
}
