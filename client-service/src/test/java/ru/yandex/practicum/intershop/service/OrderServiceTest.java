package ru.yandex.practicum.intershop.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.db.dao.OrderDao;
import ru.yandex.practicum.intershop.db.repository.OrderItemRepository;
import ru.yandex.practicum.intershop.db.repository.OrderRepository;
import ru.yandex.practicum.intershop.db.repository.ProductRepository;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.entity.Order;
import ru.yandex.practicum.intershop.entity.Product;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {OrderService.class})
class OrderServiceTest {
    @Autowired
    OrderService orderService;
    @MockitoBean
    OrderRepository orderRepository;
    @MockitoBean
    OrderItemRepository orderItemRepository;
    @MockitoBean
    ProductRepository productRepository;
    @MockitoBean
    CartService cartService;
    @MockitoBean
    OrderDao orderDao;
    @MockitoBean
    WebSession session;


    @Test
    void placeOrder() {
        when(cartService.isEmpty(session)).thenReturn(Mono.just(Boolean.FALSE));
        when(cartService.getProductIdsInCart(session)).thenReturn(Flux.fromStream(Stream.of(1L, 2L)));
        when(cartService.getProductQuantity(anyLong(), any(WebSession.class)))
                .thenReturn(Mono.just(2))
                .thenReturn(Mono.just(1));
        Product one = mock(Product.class);
        Product two = mock(Product.class);
        when(one.getPrice()).thenReturn(BigDecimal.valueOf(100));
        when(two.getPrice()).thenReturn(BigDecimal.valueOf(500));
        when(productRepository.findAllById(any(Flux.class))).thenReturn(Flux.just(one, two));
        Order order = new Order().setId(1L);
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));
        when(orderItemRepository.saveAll(any(Iterable.class))).thenReturn(Flux.empty());
        when(cartService.clearCart(any())).thenReturn(Mono.empty());
        Mono<Long> result = orderService.placeOrder(session);

        StepVerifier.create(result)
                .assertNext(s -> {
                    ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
                    verify(orderRepository, times(1)).save(argumentCaptor.capture());
                    Order value = argumentCaptor.getValue();
                    assertEquals(new BigDecimal(700), value.getTotalPrice());
                    assertNotNull(value.getNumber());
                    verify(orderItemRepository, times(1)).saveAll(any(Iterable.class));
                    verify(cartService, times(1)).clearCart(any(WebSession.class));
                }).verifyComplete();
    }

    @Test
    void getOrder() {
        when(orderDao.findOrderWithProducts(anyLong())).thenReturn(Mono.just(Mockito.mock(OrderDto.class)));
        Mono<OrderDto> result = orderService.getOrder(1L);

        StepVerifier.create(result)
                        .assertNext(o -> {
                            verify(orderDao, times(1)).findOrderWithProducts(1L);
                        }).verifyComplete();
    }

    @Test
    void getOrders() {
        when(orderDao.findAllOrdersWithItems()).thenReturn(Flux.just(Mockito.mock(OrderItemDto.class)));
        Flux<OrderItemDto> result = orderService.getOrders();
        StepVerifier.create(result)
                .assertNext(o -> {
                    verify(orderDao, times(1)).findAllOrdersWithItems();
                }).verifyComplete();
    }
}