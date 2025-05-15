package ru.yandex.practicum.intershop.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.db.repository.ProductRepository;
import ru.yandex.practicum.intershop.dto.CreateProductDto;
import ru.yandex.practicum.intershop.dto.ProductItemDto;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.mapper.ProductMapperImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
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
    @MockitoBean
    WebSession session;

    private final Product testProduct = new Product()
            .setId(1L)
            .setImageName("imgName")
            .setTitle("title")
            .setPrice(BigDecimal.TWO.setScale(2, RoundingMode.HALF_UP))
            .setCreatedAt(LocalDateTime.now())
            .setDescription("desc");

    @Test
    void getProducts_without_search() {
        PageRequest pageRequest = Mockito.mock(PageRequest.class);
        when(productRepository.count()).thenReturn(Mono.just(1L));
        when(productRepository.findAllBy(any(PageRequest.class)))
                .thenReturn(Flux.just(testProduct));
        when(cartService.getProductQuantity(1L, session))
                .thenReturn(Mono.just(2));

        Mono<Page<ProductItemDto>> result = productService.getProducts(session, pageRequest, null);
        StepVerifier.create(result)
                        .assertNext(s -> {
                            verify(productRepository, times(1)).findAllBy(eq(pageRequest));
                            assertEquals(1, s.getContent().size());
                            ProductItemDto productItemDto = s.getContent().get(0);
                            assertProductItemDto(productItemDto, "Проверка поиска продуктов без фильтра", 2);
                        }).verifyComplete();
    }

    @Test
    void getProducts_with_search() {
        PageRequest pageRequest = Mockito.mock(PageRequest.class);
        when(productRepository.countAllByTitleContainingOrDescriptionContaining(any(), any())).thenReturn(Mono.just(1L));
        when(productRepository.findByTitleContainingOrDescriptionContaining(any(PageRequest.class), anyString(), anyString()))
                .thenReturn(Flux.just(testProduct));
        when(cartService.getProductQuantity(1L, session))
                .thenReturn(Mono.just(2));

        Mono<Page<ProductItemDto>> result = productService.getProducts(session, pageRequest, "search");
        StepVerifier.create(result)
                .assertNext(s -> {
                    verify(productRepository, times(1))
                            .findByTitleContainingOrDescriptionContaining(eq(pageRequest), anyString(), any());
                    assertEquals(1, s.getContent().size());
                    ProductItemDto productItemDto = s.getContent().get(0);
                    assertProductItemDto(productItemDto, "Проверка поиска продуктов c фильтром", 2);
                }).verifyComplete();
    }

    @Test
    void getProduct() {
        when(productRepository.findById(anyLong())).thenReturn(Mono.just(testProduct));
        when(cartService.getProductQuantity(1L, session)).thenReturn(Mono.just(2));

        Mono<ProductItemDto> result = productService.getProduct(session, 1L);

        StepVerifier.create(result)
                .assertNext(s -> {
                    verify(productRepository, times(1)).findById(anyLong());
                    assertProductItemDto(s, "Проверка получения продукта", 2);
                }).verifyComplete();

    }

    @Test
    void createProduct() {
        when(productRepository.save(any())).thenReturn(Mono.just(testProduct));
        when(documentService.save(any())).thenReturn(Mono.just("newImage"));

        Mono<FilePart> filePart = Mono.just(mock(FilePart.class));
        Mono<Long> result = productService.createProduct(new CreateProductDto()
                .setDescription("1")
                .setTitle("2")
                .setPrice(BigDecimal.TEN), filePart);

        StepVerifier.create(result)
                        .assertNext(s -> {
                            verify(documentService, times(1)).save(filePart);
                            verify(productRepository, times(1)).save(any(Product.class));
                        }).verifyComplete();
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