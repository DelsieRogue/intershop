package ru.yandex.practicum.intershop.service;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import ru.yandex.practicum.intershop.dto.CreateProductDto;
import ru.yandex.practicum.intershop.dto.ProductItemDto;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.mapper.ProductMapper;
import ru.yandex.practicum.intershop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CartService cartService;
    private final DocumentService documentService;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper,
                          CartService cartService, DocumentService documentService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.cartService = cartService;
        this.documentService = documentService;
    }

    public Page<ProductItemDto> getProducts(PageRequest pageRequest, @Nullable String search) {
        Page<Product> productPage  = StringUtils.isEmpty(search)
                ? productRepository.findAll(pageRequest)
                : productRepository.findByTitleContainingOrDescriptionContaining(pageRequest, search, search);
        return productPage.map(p -> productMapper.toProductItemDto(p, cartService.getProductQuantity(p.getId())));
    }

    public ProductItemDto getProduct(Long productId) {
        return productRepository.findById(productId)
                .map(p -> productMapper.toProductItemDto(p, cartService.getProductQuantity(p.getId())))
                .orElseThrow();
    }

    @Transactional
    public Long createProduct(CreateProductDto productDto, MultipartFile image) {
        String imageName = documentService.save(image).orElse(null);
        Product product = productRepository.save(productMapper.toProduct(productDto, imageName));
        return product.getId();
    }
}
