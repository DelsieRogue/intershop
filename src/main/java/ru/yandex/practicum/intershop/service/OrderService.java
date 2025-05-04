package ru.yandex.practicum.intershop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.entity.Order;
import ru.yandex.practicum.intershop.entity.OrderItem;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.mapper.OrderMapper;
import ru.yandex.practicum.intershop.repository.OrderRepository;
import ru.yandex.practicum.intershop.repository.ProductRepository;
import ru.yandex.practicum.intershop.utils.DataBaseRequestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        CartService cartService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public Long placeOrder() {
        Order order = new Order();
        if (cartService.isEmpty()) {
            throw new IllegalStateException("В корзине нет товаров");
        }
        List<Product> products = productRepository.findAllById(cartService.getProductIdsInCart());
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Product product : products) {
            Integer quantity = cartService.getProductQuantity(product.getId());
            OrderItem orderItem = new OrderItem().setOrder(order)
                    .setPrice(product.getPrice())
                    .setQuantity(quantity)
                    .setProduct(product);
            order.getOrderItems().add(orderItem);
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }
        order.setTotalPrice(totalPrice);
        order.setNumber(generateOrderNumber());
        orderRepository.save(order);
        cartService.clearCart();
        return order.getId();
    }

    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId).map(orderMapper::toOrderDto).orElseThrow();
    }

    public List<OrderItemDto> getOrders() {
        return orderRepository.findAll(DataBaseRequestUtils.defaultSort())
                .stream().map(orderMapper::toOrderItemDto).collect(Collectors.toList());
    }

    public String generateOrderNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
