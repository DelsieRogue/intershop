package ru.yandex.practicum.intershop.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "products")
public class Product {
    @Id
    @EqualsAndHashCode.Include
    private Long id;
    private String title;
    private String description;
    private String imageName;
    private BigDecimal price;
    private LocalDateTime createdAt;
}
