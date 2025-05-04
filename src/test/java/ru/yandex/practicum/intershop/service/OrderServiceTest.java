package ru.yandex.practicum.intershop.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.entity.Order;
import ru.yandex.practicum.intershop.entity.OrderItem;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.mapper.OrderItemMapper;
import ru.yandex.practicum.intershop.mapper.OrderItemMapperImpl;
import ru.yandex.practicum.intershop.mapper.OrderMapper;
import ru.yandex.practicum.intershop.mapper.OrderMapperImpl;
import ru.yandex.practicum.intershop.repository.OrderRepository;
import ru.yandex.practicum.intershop.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {OrderService.class, OrderMapperImpl.class, OrderItemMapperImpl.class})
class OrderServiceTest {
    @Autowired
    OrderService orderService;
    @MockitoBean
    OrderRepository orderRepository;
    @MockitoBean
    ProductRepository productRepository;
    @MockitoBean
    CartService cartService;

    private final Order testOrder = new Order().setId(3L)
            .setTotalPrice(BigDecimal.TEN)
            .setNumber("N1234")
            .setCreatedAt(LocalDateTime.now())
            .setOrderItems(List.of(new OrderItem()
                    .setId(2L)
                    .setPrice(BigDecimal.ONE)
                    .setQuantity(1)
                    .setProduct(new Product().setId(1L)
                            .setDescription("desc")
                            .setTitle("title")
                            .setPrice(BigDecimal.TWO)
                            .setImageName("img"))
                    .setOrder(new Order().setId(1L))));

    @Test
    void placeOrder() {
        when(cartService.isEmpty()).thenReturn(Boolean.FALSE);
        when(cartService.getProductIdsInCart()).thenReturn(Set.of(1L, 2L));
        when(cartService.getProductQuantity(any())).thenReturn(2, 1);
        Product one = mock(Product.class);
        Product two = mock(Product.class);
        when(one.getPrice()).thenReturn(BigDecimal.valueOf(100));
        when(two.getPrice()).thenReturn(BigDecimal.valueOf(500));
        when(productRepository.findAllById(any())).thenReturn(List.of(one, two));
        orderService.placeOrder();

        ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(argumentCaptor.capture());
        Order value = argumentCaptor.getValue();
        assertEquals(new BigDecimal(700), value.getTotalPrice());
        assertEquals(2, value.getOrderItems().size());
        assertNotNull(value.getNumber());
        verify(cartService).clearCart();
    }

    @Test
    void getOrder() {
        when(orderRepository.findById(any()))
                .thenReturn(Optional.of(testOrder));
        OrderDto order = orderService.getOrder(1L);
        assertAll("Проверка получения заказа (OrderDto)",
                () -> assertEquals(3L, order.getId()),
                () -> assertEquals("N1234", order.getNumber()),
                () -> assertEquals("10.00", order.getTotalPrice()),
                () -> assertEquals(1, order.getItems().size())
        );
        OrderDto.ProductViewDto productViewDto = order.getItems().get(0);
        assertAll("Проверка получения заказа (OrderDto.ProductViewDto)",
                () -> assertEquals(1, productViewDto.getId()),
                () -> assertEquals(1, productViewDto.getQuantity()),
                () -> assertEquals("1.00", productViewDto.getTotalPrice()),
                () -> assertEquals("1.00", productViewDto.getPrice()),
                () -> assertEquals("title", productViewDto.getTitle()),
                () -> assertEquals("desc", productViewDto.getDescription()),
                () -> assertEquals("img", productViewDto.getImageName()));
    }

    @Test
    void getOrders() {
        when(orderRepository.findAll(any(Sort.class))).thenReturn(List.of(testOrder));
        List<OrderItemDto> orders = orderService.getOrders();
        assertEquals(1, orders.size());
        OrderItemDto orderItemDto = orders.get(0);
        assertAll("Проверка получения заказов (OrderItemDto)",
                () -> assertEquals("N1234", orderItemDto.getNumber()),
                () -> assertEquals("10.00", orderItemDto.getTotalPrice()),
                () -> assertEquals(3L, orderItemDto.getId()));
        assertEquals(1, orders.get(0).getItems().size());
        OrderItemDto.ProductViewDto productViewDto = orderItemDto.getItems().get(0);
        assertAll("Проверка получения заказов (OrderItemDto)",
                () -> assertEquals("1.00", productViewDto.getPrice()),
                () -> assertEquals("title", productViewDto.getTitle()),
                () -> assertEquals(1, productViewDto.getQuantity()));
    }

    @Test
    void generateOrderNumber() {
        assertEquals(10, orderService.generateOrderNumber().length());
    }
}