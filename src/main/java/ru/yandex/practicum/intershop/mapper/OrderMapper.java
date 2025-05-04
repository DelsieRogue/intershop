package ru.yandex.practicum.intershop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.entity.Order;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "totalPrice", expression = "java(order.getTotalPrice().setScale(2, java.math.RoundingMode.HALF_UP).toString())")
    OrderDto toOrderDto(Order order);

    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "totalPrice", expression = "java(order.getTotalPrice().setScale(2, java.math.RoundingMode.HALF_UP).toString())")
    OrderItemDto toOrderItemDto(Order order);
}
