package ru.yandex.practicum.intershop.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.entity.Order;
import ru.yandex.practicum.intershop.entity.OrderItem;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.integration.AbstractDataJpaTest;
import ru.yandex.practicum.intershop.repository.OrderItemRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class OrderItemRepositoryTest extends AbstractDataJpaTest {

    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    void save() {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(new Product().setId(1L));
        orderItem.setOrder(new Order().setId(2L));
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(19.99));
        orderItemRepository.save(orderItem);
        assertNotNull(orderItem.getId());
    }

    @Test
    void findById() {
        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(1L);
        assertTrue(foundOrderItem.isPresent());
    }

}