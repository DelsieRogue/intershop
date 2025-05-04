package ru.yandex.practicum.intershop.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.integration.AbstractDataJpaTest;
import ru.yandex.practicum.intershop.integration.AbstractTestContainerTest;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.repository.ProductRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ProductRepositoryTest extends AbstractDataJpaTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void findByTitleContainingOrDescriptionContaining() {
        Page<Product> product = productRepository.findByTitleContainingOrDescriptionContaining(
                PageRequest.of(0, 10), "Product", "Product"
        );
        assertEquals(9, product.getContent().size());
    }

    @Test
    void findAllById() {
        List<Product> result = productRepository.findAllById(Set.of(1L, 2L, 3L));
        assertEquals(3, result.size());
    }
}