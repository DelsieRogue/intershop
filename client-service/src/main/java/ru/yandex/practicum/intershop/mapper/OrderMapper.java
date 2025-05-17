package ru.yandex.practicum.intershop.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderMapper {

    public static OrderDto toOrderDto(List<Map<String, Object>> list) {
        List<OrderDto.ProductViewDto> items = list.stream()
                .map(item -> new OrderDto.ProductViewDto()
                        .setId((Long) item.get("productId"))
                        .setTitle((String) item.get("title"))
                        .setDescription((String) item.get("description"))
                        .setImageName((String) item.get("imageName"))
                        .setPrice(((BigDecimal) item.get("price")).setScale(2, RoundingMode.HALF_UP).toString())
                        .setTotalPrice(((BigDecimal) item.get("price"))
                                .multiply(BigDecimal.valueOf((Integer) item.get("quantity")))
                                .setScale(2, RoundingMode.HALF_UP).toString())
                        .setQuantity((Integer) item.get("quantity")))
                .collect(Collectors.toList());

        Map<String, Object> first = list.get(0);
        OrderDto order = new OrderDto();
        order.setId((Long) first.get("orderId"));
        order.setNumber((String) first.get("number"));
        order.setTotalPrice(((BigDecimal) first.get("totalPrice")).setScale(2, RoundingMode.HALF_UP).toString());
        order.setItems(items);
        return order;
    }

    public static OrderItemDto toOrderItemDto(List<Map<String, Object>> list) {
        List<OrderItemDto.ProductViewDto> items = list.stream().map(item -> new OrderItemDto.ProductViewDto()
                .setPrice(((BigDecimal) item.get("price")).setScale(2, RoundingMode.HALF_UP).toString())
                .setTitle((String) item.get("title"))
                .setQuantity((Integer) item.get("quantity"))).collect(Collectors.toList());
        Map<String, Object> first = list.get(0);
        return new OrderItemDto()
                .setItems(items)
                .setTotalPrice(((BigDecimal) first.get("totalPrice")).setScale(2, RoundingMode.HALF_UP).toString())
                .setId((Long) first.get("orderId"))
                .setNumber((String) first.get("number"))
                .setTotalPrice(((BigDecimal) first.get("totalPrice")).setScale(2, RoundingMode.HALF_UP).toString());
    }
}
