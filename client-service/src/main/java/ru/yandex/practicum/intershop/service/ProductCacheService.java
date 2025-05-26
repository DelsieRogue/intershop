package ru.yandex.practicum.intershop.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.db.repository.ProductRepository;
import ru.yandex.practicum.intershop.entity.Product;

import java.util.HashSet;

@Service
public class ProductCacheService {
    private final ProductRepository productRepository;
    private final ReactiveRedisTemplate<String, Product> redisTemplate;
    private final ReactiveRedisTemplate<String, Product[]> redisTemplateArray;
    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String PRODUCT_SEARCH_KEY_PREFIX = "product:search:";
    private static final String PRODUCT_PAGE_KEY_PREFIX = "product:page:";
    private static final String PRODUCT_IDS_KEY_PREFIX = "product:ids:";

    public ProductCacheService(ProductRepository productRepository,
                             ReactiveRedisTemplate<String, Product> redisTemplate,
                             ReactiveRedisTemplate<String, Product[]> redisTemplateArray) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
        this.redisTemplateArray = redisTemplateArray;
    }

    public Flux<Product> findByTitleContainingOrDescriptionContaining(PageRequest page, String search) {
        String cacheKey = PRODUCT_SEARCH_KEY_PREFIX + search.hashCode() + ":" + page.hashCode();
        
        return redisTemplateArray.opsForValue().get(cacheKey)
                .switchIfEmpty(
                    productRepository.findByTitleContainingOrDescriptionContaining(page, search, search)
                        .collectList()
                        .flatMap(products -> 
                            redisTemplateArray.opsForValue()
                                .set(cacheKey, products.toArray(Product[]::new))
                                .thenReturn(products.toArray(Product[]::new))
                        )
                )
                .flatMapMany(Flux::fromArray);
    }

    public Flux<Product> findAllBy(PageRequest page) {
        String cacheKey = PRODUCT_PAGE_KEY_PREFIX + page.hashCode();
        
        return redisTemplateArray.opsForValue().get(cacheKey)
                .switchIfEmpty(
                    productRepository.findAllBy(page)
                        .collectList()
                        .flatMap(products -> 
                            redisTemplateArray.opsForValue()
                                .set(cacheKey, products.toArray(Product[]::new))
                                .thenReturn(products.toArray(Product[]::new))
                        )
                )
                .flatMapMany(Flux::fromArray);
    }

    public Flux<Product> findAllById(Flux<Long> ids) {
        return ids.collect(HashSet::new, HashSet::add)
                .flatMapMany(set -> {
                    String cacheKey = PRODUCT_IDS_KEY_PREFIX + set.hashCode();
                    return redisTemplateArray.opsForValue().get(cacheKey)
                            .switchIfEmpty(
                                productRepository.findAllById(ids)
                                    .collectList()
                                    .flatMap(products -> 
                                        redisTemplateArray.opsForValue()
                                            .set(cacheKey, products.toArray(Product[]::new))
                                            .thenReturn(products.toArray(Product[]::new))
                                    )
                            )
                            .flatMapMany(Flux::fromArray);
                });
    }

    public Mono<Product> findById(Long id) {
        String cacheKey = PRODUCT_KEY_PREFIX + id;
        
        return redisTemplate.opsForValue().get(cacheKey)
                .switchIfEmpty(
                    productRepository.findById(id)
                        .flatMap(product -> 
                            redisTemplate.opsForValue()
                                .set(cacheKey, product)
                                .thenReturn(product)
                        )
                );
    }

    public Mono<Product> save(Product entity) {
        return productRepository.save(entity)
                .flatMap(savedProduct -> 
                    redisTemplate.opsForValue()
                        .set(PRODUCT_KEY_PREFIX + savedProduct.getId(), savedProduct)
                        .thenReturn(savedProduct)
                );
    }
}
