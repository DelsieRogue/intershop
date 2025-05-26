package ru.yandex.practicum.intershop.db.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.entity.Product;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {

    Mono<Long> countAllByTitleContainingOrDescriptionContaining(String searchTitle, String searchDesc);
    Flux<Product> findByTitleContainingOrDescriptionContaining(Pageable page, String searchTitle, String searchDesc);
    Flux<Product> findAllBy(Pageable page);
}
