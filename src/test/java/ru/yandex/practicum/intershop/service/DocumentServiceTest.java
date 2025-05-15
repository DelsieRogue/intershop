package ru.yandex.practicum.intershop.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = DocumentService.class)
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Test
    void save() {
        FilePart filePart = Mockito.mock(FilePart.class);
        when(filePart.transferTo(any(File.class))).thenReturn(Mono.empty());
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.getContentLength()).thenReturn(10L);
        when(filePart.headers()).thenReturn(headers);
        when(filePart.filename()).thenReturn("names.png");
        Mono<String> result = documentService.save(Mono.just(filePart));
        StepVerifier.create(result)
                        .assertNext(s -> {
                            verify(filePart, times(1)).transferTo(any(File.class));
                            Assertions.assertTrue(s.startsWith("file_"));
                            Assertions.assertTrue(s.endsWith(".png"));
                        }).verifyComplete();
    }

    @Test
    void save_empty() {
        FilePart filePart = Mockito.mock(FilePart.class);
        HttpHeaders headers = mock(HttpHeaders.class);
        when(headers.getContentLength()).thenReturn(0L);
        when(filePart.headers()).thenReturn(headers);
        when(filePart.filename()).thenReturn("names.png");
        Mono<String> result = documentService.save(Mono.just(filePart));
        String block = result.block();
        verify(filePart, times(0)).transferTo(any(File.class));
        Assertions.assertNull(block);
    }

}