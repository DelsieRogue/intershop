package ru.yandex.practicum.intershop.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.db.repository.ProductRepository;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.integration.AbstractRepositoryTest;

class ProductRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void findByTitleContainingOrDescriptionContaining() {
        PageRequest request = PageRequest.of(0, 10);
        Flux<Product> productFlux = productRepository
                .findByTitleContainingOrDescriptionContaining(request, "Product 1", "Product 1");

        StepVerifier.create(productFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void countAllByTitleContainingOrDescriptionContaining() {
        Mono<Long> product = productRepository
                .countAllByTitleContainingOrDescriptionContaining("Product 1", "Product 1");

        StepVerifier.create(product)
                .expectNext(1L)
                .verifyComplete();

    }

    @Test
    void findAllById() {
        PageRequest request = PageRequest.of(0, 10);
        Flux<Product> productFlux = productRepository.findAllBy(request);

        StepVerifier.create(productFlux)
                .expectNextCount(9)
                .verifyComplete();
    }
}