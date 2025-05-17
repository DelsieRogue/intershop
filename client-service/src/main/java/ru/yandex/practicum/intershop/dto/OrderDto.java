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
public class OrderDto {
    private Long id;
    private String number;
    private String totalPrice;
    private List<ProductViewDto> items;

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductViewDto {
        private Long id;
        private String imageName;
        private String title;
        private String description;
        private String price;
        private Integer quantity;
        private String totalPrice;
    }
}
