package ru.yandex.practicum.intershop.integration.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.db.dao.OrderDao;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.integration.AbstractRepositoryTest;

public class OrderDaoTest extends AbstractRepositoryTest {
    @Autowired
    OrderDao orderDao;

    @Test
    void findOrderWithProducts() {
        Mono<OrderDto> orderDtoMono = orderDao.findOrderWithProducts(1L);
        StepVerifier.create(orderDtoMono)
                .assertNext(s -> {
                    Assertions.assertEquals("379.94", s.getTotalPrice());
                    Assertions.assertEquals("ORD12345", s.getNumber());
                    Assertions.assertEquals(1L, s.getId());
                    Assertions.assertEquals(3, s.getItems().size());
                })
                .verifyComplete();
    }

    @Test
    void findAllOrdersWithItems() {
        Flux<OrderItemDto> orderItemDtoFlux = orderDao.findAllOrdersWithItems();
        StepVerifier.create(orderItemDtoFlux)
                .expectNextMatches(dto -> dto.getNumber().equals("ORD12345"))
                .expectNextMatches(dto -> dto.getNumber().equals("ORD12346"))
                .expectNextMatches(dto -> dto.getNumber().equals("ORD12347"))
                .expectNextMatches(dto -> dto.getNumber().equals("ORD12348"))
                .expectNextMatches(dto -> dto.getNumber().equals("ORD12349"))
                .verifyComplete();
    }

}
