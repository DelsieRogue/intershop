package ru.yandex.practicum.intershop.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
public abstract class AbstractControllerMvcTest extends AbstractTestContainerTest {
    @Autowired
    protected WebTestClient webTestClient;
}
