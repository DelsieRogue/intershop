package ru.yandex.practicum.intershop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.entity.OrderItem;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = BigDecimal.class)
public interface OrderItemMapper {

    @Mapping(target = "imageName", source = "product.imageName")
    @Mapping(target = "title",source = "product.title")
    @Mapping(target = "id",source = "product.id")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "price", expression = "java(orderItem.getPrice().setScale(2, java.math.RoundingMode.HALF_UP).toString())")
    @Mapping(target = "totalPrice", expression = "java(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))" +
            ".setScale(2, java.math.RoundingMode.HALF_UP).toString())")
    OrderDto.ProductViewDto toOrderProductViewDto(OrderItem orderItem);

    @Mapping(target = "title",source = "product.title")
    @Mapping(target = "price", expression = "java(orderItem.getPrice().setScale(2, java.math.RoundingMode.HALF_UP).toString())")
    OrderItemDto.ProductViewDto toOrderItemProductViewDto(OrderItem orderItem);
}
