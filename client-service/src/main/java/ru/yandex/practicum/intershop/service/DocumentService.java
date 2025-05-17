package ru.yandex.practicum.intershop.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class DocumentService {
    private final String serverPath;

    public DocumentService(@Value("${image.folder.server.path}") String serverPath) {
        this.serverPath = serverPath;
    }

    public Mono<String> save(Mono<FilePart> image) {
        return image.flatMap(img -> {
            if (img.headers().getContentLength() == 0) {
                log.warn("Файл отсутствует/пустой, загрузка не выполнена.");
                return Mono.empty();
            }
            String extension = FilenameUtils.getExtension(img.filename());
            String fileName = "file_" + UUID.randomUUID() + "." + extension;
            File destinationFile = new File(serverPath, fileName);
            return img.transferTo(destinationFile).thenReturn(fileName);
        })
                .doOnError(e -> log.error("Ошибка сохранения файла", e))
                .onErrorMap(IOException.class, e -> new IllegalStateException("Ошибка сохранения файла", e));
    }
}
