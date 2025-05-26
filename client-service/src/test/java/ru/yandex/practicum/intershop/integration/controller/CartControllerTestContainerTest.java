package ru.yandex.practicum.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.integration.AbstractControllerMvcTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class CartControllerTestContainerTest extends AbstractControllerMvcTest {

    @Test
    void getCart() {
        when(paymentApi.getBalance())
                .thenReturn(Mono.just(BigDecimal.valueOf(100)))
                .thenReturn(Mono.just(BigDecimal.valueOf(1)));
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
                    assertFalse(response.getResponseBody().contains("Сумма заказа превышает баланс"));
                    assertTrue(response.getResponseBody().contains("Product 1"));
                });
        webTestClient.get()
                .uri("/cart")
                .cookie("SESSION", session)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class)
                .consumeWith(response -> {
                    assertTrue(response.getResponseBody().contains("Сумма заказа превышает баланс"));
                    assertTrue(response.getResponseBody().contains("Product 1"));
                });
    }

    @Test
    void placeOrder() {
        when(paymentApi.processPayment(any())).thenReturn(Mono.empty());
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