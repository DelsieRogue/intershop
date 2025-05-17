package ru.yandex.practicum.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import ru.yandex.practicum.intershop.integration.AbstractControllerMvcTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CartControllerTestContainerTest extends AbstractControllerMvcTest {


    @Test
    void getCart() {
        FluxExchangeResult<Void> exchangeResult = webTestClient.put()
                .uri("/product/{id}/updateInCart",1)
                .bodyValue("PLUS")
                .header("Referer", "/product")
                .exchange().expectStatus().is3xxRedirection().returnResult(Void.class);
        String session = exchangeResult.getResponseCookies().getFirst("SESSION").getValue();
        webTestClient.get()
                .uri("/cart")
                .cookie("SESSION", session)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class)
                .consumeWith(response -> {
                    assertTrue(response.getResponseBody().contains("Product 1"));
                });
    }

    @Test
    void placeOrder() {
        FluxExchangeResult<Void> exchangeResult = webTestClient.put()
                .uri("/product/{id}/updateInCart",1)
                .bodyValue("PLUS")
                .header("Referer", "/product")
                .exchange().expectStatus().is3xxRedirection().returnResult(Void.class);
        String session = exchangeResult.getResponseCookies().getFirst("SESSION").getValue();
        webTestClient.post()
                .uri("/cart/confirm")
                .cookie("SESSION", session)
                .exchange()
                .expectStatus().is3xxRedirection();
    }

}