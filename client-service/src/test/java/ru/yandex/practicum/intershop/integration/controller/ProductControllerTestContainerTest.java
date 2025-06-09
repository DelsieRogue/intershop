package ru.yandex.practicum.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.reactive.function.BodyInserters;
import ru.yandex.practicum.intershop.integration.AbstractControllerMvcTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProductControllerTestContainerTest extends AbstractControllerMvcTest {

    @Test
    void getProducts() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/product")
                        .queryParam("sort", "price")
                        .queryParam("search", "1")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class)
                .consumeWith(response -> assertTrue(response.getResponseBody().contains("<img")));
    }

    @Test
    void updateInCartAnon() {
        webTestClient.put()
                .uri("/product/{id}/updateInCart",1)
                .bodyValue("PLUS")
                .header("Referer", "/product")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login");
    }

    @Test
    @WithUserDetails(
            value = "test_user",
            userDetailsServiceBeanName = "customUserDetailsService",
            setupBefore = TestExecutionEvent.TEST_METHOD
    )
    void updateInCart() {
        webTestClient.put()
                .uri("/product/{id}/updateInCart",1)
                .bodyValue("PLUS")
                .header("Referer", "/product")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/product");
    }

    @Test
    void getProduct() {
        webTestClient.get()
                .uri("/product/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class)
                .consumeWith(response -> {
                    assertTrue(response.getResponseBody().contains("Product 1"));
                });
    }

    @Test
    @WithUserDetails(
            value = "test_admin",
            userDetailsServiceBeanName = "customUserDetailsService",
            setupBefore = TestExecutionEvent.TEST_METHOD
    )
    void createProduct() throws IOException {
        webTestClient.post()
                .uri("/product")
                .header("Referer", "/product")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("price", "1000")
                        .with("title", "Test")
                        .with("description", "Test description"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/product");
    }
}