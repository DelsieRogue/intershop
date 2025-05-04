package ru.yandex.practicum.intershop.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.intershop.dto.CreateProductDto;
import ru.yandex.practicum.intershop.dto.ProductItemDto;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.mapper.ProductMapperImpl;
import ru.yandex.practicum.intershop.repository.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ProductService.class, ProductMapperImpl.class})
class ProductServiceTest {

    @Autowired
    ProductService productService;
    @MockitoBean
    ProductRepository productRepository;
    @MockitoBean
    CartService cartService;
    @MockitoBean
    DocumentService documentService;

    private final Product testProduct = new Product()
            .setId(1L)
            .setImageName("imgName")
            .setTitle("title")
            .setPrice(BigDecimal.TWO.setScale(2, RoundingMode.HALF_UP))
            .setCreatedAt(LocalDateTime.now())
            .setDescription("desc");

    @Test
    void getProducts_without_search() {
        PageRequest mock = Mockito.mock(PageRequest.class);
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(testProduct)));
        when(cartService.getProductQuantity(1L)).thenReturn(2);
        Page<ProductItemDto> products = productService.getProducts(mock, null);
        verify(productRepository, times(1)).findAll(eq(mock));

        assertEquals(1, products.getContent().size());
        ProductItemDto productItemDto = products.getContent().get(0);
        assertProductItemDto(productItemDto, "Проверка поиска продуктов без фильтра", 2);
    }

    @Test
    void getProducts_with_search() {
        PageRequest mock = Mockito.mock(PageRequest.class);
        when(productRepository.findByTitleContainingOrDescriptionContaining(any(PageRequest.class), anyString(), anyString()))
                .thenReturn(new PageImpl<>(List.of(testProduct)));
        when(cartService.getProductQuantity(1L)).thenReturn(2);
        Page<ProductItemDto> products = productService.getProducts(mock, "search");
        verify(productRepository, times(1)).findByTitleContainingOrDescriptionContaining(eq(mock), anyString(), anyString());

        assertEquals(1, products.getContent().size());
        ProductItemDto productItemDto = products.getContent().get(0);
        assertProductItemDto(productItemDto, "Проверка поиска продуктов c фильтром", 2);
    }

    @Test
    void getProduct() {
        when(productRepository.findById(any())).thenReturn(Optional.of(testProduct));
        when(cartService.getProductQuantity(1L)).thenReturn(2);

        ProductItemDto product = productService.getProduct(1L);

        verify(productRepository, times(1)).findById(anyLong());
        assertProductItemDto(product, "Проверка получения продукта", 2);
    }

    @Test
    void createProduct() {
        when(productRepository.save(any())).thenReturn(testProduct);
        when(documentService.save(any())).thenReturn(Optional.of("newImage"));

        MultipartFile multipartFile = mock(MultipartFile.class);
        productService.createProduct(new CreateProductDto()
                .setDescription("1")
                .setTitle("2")
                .setPrice(BigDecimal.TEN), multipartFile);

        verify(documentService, times(1)).save(multipartFile);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    private void assertProductItemDto(ProductItemDto productItemDto, String nameTest, Integer quantity) {
        assertAll(nameTest,
                () -> assertEquals(testProduct.getId(), productItemDto.getId()),
                () -> assertEquals(testProduct.getPrice(), new BigDecimal(productItemDto.getPrice())),
                () -> assertEquals(testProduct.getTitle(), productItemDto.getTitle()),
                () -> assertEquals(testProduct.getDescription(), productItemDto.getDescription()),
                () -> assertEquals(testProduct.getImageName(), productItemDto.getImageName()),
                () -> assertEquals(quantity, productItemDto.getQuantity()));
    }
}