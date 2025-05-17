package ru.yandex.practicum.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.intershop.integration.AbstractControllerMvcTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OrderControllerTestContainerTest extends AbstractControllerMvcTest {

    @Test
    void getOrder() {
        webTestClient.get()
                .uri("/order/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class)
                .consumeWith(response -> {
                    assertTrue(response.getResponseBody().contains("<tr>"));
                });
    }

    @Test
    void getOrders() {
        webTestClient.get()
                .uri("/order")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class)
                .consumeWith(response -> {
                    assertTrue(response.getResponseBody().contains("<tr>"));
                });
    }
}