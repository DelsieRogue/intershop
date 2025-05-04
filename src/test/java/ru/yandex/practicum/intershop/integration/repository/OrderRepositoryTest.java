package ru.yandex.practicum.intershop.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.entity.Order;
import ru.yandex.practicum.intershop.entity.OrderItem;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.integration.AbstractDataJpaTest;
import ru.yandex.practicum.intershop.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
class OrderRepositoryTest extends AbstractDataJpaTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void save() {
        Order order = new Order()
                .setNumber("1234567")
                .setOrderItems(List.of(new OrderItem()
                        .setPrice(BigDecimal.valueOf(1000))
                        .setProduct(new Product().setId(1L))
                        .setOrder(new Order().setId(3L))
                        .setQuantity(4)))
                .setTotalPrice(BigDecimal.valueOf(4000));
        orderRepository.save(order);

        Optional<Order> found = orderRepository.findById(order.getId());
        assertTrue(found.isPresent());
        found.ifPresent(f -> assertAll("Проверка сохранённого заказа",
                () -> assertNotNull(order.getId()),
                () -> assertEquals(1, order.getOrderItems().size())
        ));
    }

    @Test
    void findAll() {
        List<Order> all = orderRepository.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    void findById() {
        Optional<Order> order = orderRepository.findById(1L);
        assertTrue(order.isPresent());
    }
}