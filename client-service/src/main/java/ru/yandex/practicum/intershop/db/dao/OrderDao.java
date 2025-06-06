package ru.yandex.practicum.intershop.db.dao;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.mapper.OrderMapper;

@Repository
public class OrderDao {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public OrderDao(R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    public Mono<OrderDto> findOrderWithProducts(Long orderId) {
        String sql = """
                    SELECT o.id as orderId, o.user_id as userId, o.number as number, o.total_price as totalPrice,
                    p.id as productId, p.image_name as imageName, p.title as title,
                    p.description as description, oi.price as price, oi.quantity as quantity
                    FROM orders o
                    JOIN order_items oi ON o.id = oi.order_id
                    JOIN products p ON oi.product_id = p.id
                    WHERE o.id = :orderId
                """;
        return r2dbcEntityTemplate.getDatabaseClient().sql(sql)
                .bind("orderId", orderId)
                .fetch()
                .all()
                .bufferUntilChanged(result -> result.get("orderId"))
                .map(OrderMapper::toOrderDto)
                .single();
    }

    public Flux<OrderItemDto> findAllOrdersWithItems(Long userId) {
        String sql = """
                    SELECT o.id as orderId, o.number as number, o.total_price as totalPrice, 
                    p.id as productId, p.title as title, oi.price as price, oi.quantity as quantity
                    FROM orders o
                    JOIN order_items oi ON o.id = oi.order_id
                    JOIN products p ON oi.product_id = p.id
                    WHERE o.user_id = :userId
                    ORDER BY o.id
                """;
        return r2dbcEntityTemplate.getDatabaseClient().sql(sql)
                .bind("userId", userId)
                .fetch()
                .all()
                .bufferUntilChanged(result -> result.get("orderId"))
                .map(OrderMapper::toOrderItemDto);
    }

    public Flux<OrderItemDto> findAllOrdersWithItems() {
        String sql = """
                    SELECT o.id as orderId, o.number as number, o.total_price as totalPrice, 
                    p.id as productId, p.title as title, oi.price as price, oi.quantity as quantity
                    FROM orders o
                    JOIN order_items oi ON o.id = oi.order_id
                    JOIN products p ON oi.product_id = p.id
                    ORDER BY o.id
                """;
        return r2dbcEntityTemplate.getDatabaseClient().sql(sql)
                .fetch()
                .all()
                .bufferUntilChanged(result -> result.get("orderId"))
                .map(OrderMapper::toOrderItemDto);
    }

}
