package ru.yandex.practicum.intershop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.db.dao.OrderDao;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.entity.Order;
import ru.yandex.practicum.intershop.entity.OrderItem;
import ru.yandex.practicum.intershop.db.repository.OrderItemRepository;
import ru.yandex.practicum.intershop.db.repository.OrderRepository;
import ru.yandex.practicum.intershop.db.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderDao orderDao;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(OrderDao orderDao, OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository, CartService cartService) {
        this.orderDao = orderDao;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Mono<Long> placeOrder(WebSession session) {
        return productRepository.findAllById(cartService.getProductIdsInCart(session)
                        .switchIfEmpty(Mono.error(new IllegalStateException("В корзине нет товаров"))))
                .flatMap(product -> cartService.getProductQuantity(product.getId(), session)
                        .map(quantity -> new OrderItem()
                                .setProductId(product.getId())
                                .setPrice(product.getPrice())
                                .setQuantity(quantity)))
                .collectList()
                .flatMap(orderItems -> {
                    BigDecimal totalPrice = orderItems.stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Order order = new Order()
                            .setNumber(generateOrderNumber())
                            .setTotalPrice(totalPrice);
                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                orderItems.forEach(item -> item.setOrderId(savedOrder.getId()));
                                return orderItemRepository.saveAll(orderItems)
                                        .collectList()
                                        .then(cartService.clearCart(session))
                                        .thenReturn(savedOrder.getId());
                            });
                });
    }

    public Mono<OrderDto> getOrder(Long orderId) {
        return orderDao.findOrderWithProducts(orderId);
    }

    public Flux<OrderItemDto> getOrders() {
        return orderDao.findAllOrdersWithItems();
    }

    public String generateOrderNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
