package ru.yandex.practicum.intershop.service;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.CreateProductDto;
import ru.yandex.practicum.intershop.dto.ProductItemDto;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.mapper.ProductMapper;
import ru.yandex.practicum.intershop.db.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CartService cartService;
    private final DocumentService documentService;
    private final ProductCacheService productCacheService;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper,
                          CartService cartService, DocumentService documentService,
                          ProductCacheService productCacheService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.cartService = cartService;
        this.documentService = documentService;
        this.productCacheService = productCacheService;
    }

    public Mono<Page<ProductItemDto>> getProducts(WebSession session, PageRequest pageRequest, @Nullable String search) {
        Flux<Product> productFlux;
        Mono<Long> count;
        if (StringUtils.isEmpty(search)) {
            productFlux = productCacheService.findAllBy(pageRequest);
            count = productRepository.count();
        } else {
            productFlux = productCacheService.findByTitleContainingOrDescriptionContaining(pageRequest, search);
            count = productRepository.countAllByTitleContainingOrDescriptionContaining(search, search);
        }

        Flux<ProductItemDto> productDtoFlux = productFlux
                .flatMap(p -> cartService.getProductQuantity(p.getId(), session)
                        .map(quantity -> productMapper.toProductItemDto(p, quantity)));

        return Mono.zip(productDtoFlux.collectList(), count)
                .map(s -> new PageImpl<>(s.getT1(), pageRequest, s.getT2()));
    }

    public Mono<ProductItemDto> getProduct(WebSession session, Long productId) {
        return productCacheService.findById(productId)
                .flatMap(p -> cartService.getProductQuantity(p.getId(), session)
                        .map(quantity -> productMapper.toProductItemDto(p, quantity)));
    }

    @Transactional
    public Mono<Long> createProduct(CreateProductDto productDto, Mono<FilePart> image) {
        return documentService.save(image)
                .defaultIfEmpty(StringUtils.EMPTY)
                .flatMap(imgName -> productCacheService.save(productMapper
                                .toProduct(productDto, StringUtils.isNotBlank(imgName) ? imgName : null)))
                .map(Product::getId);
    }
}
