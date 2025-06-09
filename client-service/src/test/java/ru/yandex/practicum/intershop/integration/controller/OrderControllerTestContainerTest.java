package ru.yandex.practicum.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import ru.yandex.practicum.intershop.integration.AbstractControllerMvcTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OrderControllerTestContainerTest extends AbstractControllerMvcTest {

    @Test
    @WithUserDetails(
            value = "test_user",
            userDetailsServiceBeanName = "customUserDetailsService",
            setupBefore = TestExecutionEvent.TEST_METHOD
    )
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
    void getOrderAnon() {
        webTestClient.get()
                .uri("/order")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login")
                .expectBody(String.class);
    }

    @Test
    @WithUserDetails(
            value = "test_user",
            userDetailsServiceBeanName = "customUserDetailsService",
            setupBefore = TestExecutionEvent.TEST_METHOD
    )
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

    @Test
    void getOrdersAnon() {
        webTestClient.get()
                .uri("/order")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login")
                .expectBody(String.class);
    }
}