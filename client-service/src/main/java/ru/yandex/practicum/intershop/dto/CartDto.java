package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private String totalPrice;
    private List<ProductViewDto> products;

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductViewDto {
        private Long id;
        private String imageName;
        private String title;
        private String price;
        private String description;
        private Integer quantity;
    }
}
