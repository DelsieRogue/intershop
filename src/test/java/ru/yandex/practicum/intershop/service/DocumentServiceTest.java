package ru.yandex.practicum.intershop.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = DocumentService.class)
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Test
    void save() throws IOException {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("names.png");
        Optional<String> save = documentService.save(multipartFile);
        verify(multipartFile, times(1)).transferTo(any(File.class));
        assertTrue(save.isPresent() && save.get().startsWith("file") && save.get().endsWith("png"));

    }

    @Test
    void save_empty() throws IOException {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(Boolean.TRUE);
        documentService.save(multipartFile);
        verify(multipartFile, times(0)).transferTo(any(File.class));
    }

}