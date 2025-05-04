package ru.yandex.practicum.intershop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.intershop.dto.CartDto;
import ru.yandex.practicum.intershop.dto.CreateProductDto;
import ru.yandex.practicum.intershop.dto.ProductItemDto;
import ru.yandex.practicum.intershop.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "price", expression = "java(product.getPrice().setScale(2, java.math.RoundingMode.HALF_UP).toString())")
    ProductItemDto toProductItemDto(Product product, Integer quantity);

    @Mapping(target = "price", expression = "java(product.getPrice().setScale(2, java.math.RoundingMode.HALF_UP).toString())")
    CartDto.ProductViewDto toCartProductViewDto(Product product, Integer quantity);

    Product toProduct(CreateProductDto productDto, String imageName);
}
