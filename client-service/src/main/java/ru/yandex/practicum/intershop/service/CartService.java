package ru.yandex.practicum.intershop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.CartDto;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.mapper.ProductMapper;
import ru.yandex.practicum.intershop.service.cart.Cart;
import ru.yandex.practicum.payment.client.api.PaymentApi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class CartService {
    private final ProductCacheService productCacheService;
    private final ProductMapper productMapper;
    private final PaymentApi paymentApi;
    private final ConcurrentMap<Long, Cart> carts = new ConcurrentHashMap<>();

    public CartService(ProductCacheService productCacheService, ProductMapper productMapper, PaymentApi paymentApi) {
        this.productCacheService = productCacheService;
        this.productMapper = productMapper;
        this.paymentApi = paymentApi;
    }

    private Mono<Cart> getCart(Mono<Long> userId) {
        return userId.map(id -> carts.computeIfAbsent(id, key -> new Cart()));
    }

    public Mono<Void> plusToCart(Long productId, Mono<Long> userId) {
        return getCart(userId)
                .doOnNext(cart -> cart.plusProduct(productId))
                .then();
    }

    public Mono<Void> minusFromCart(Long productId, Mono<Long> userId) {
        return getCart(userId)
                .doOnNext(cart -> cart.minusProduct(productId))
                .then();
    }

    public Mono<Void> deleteFromCart(Long productId, Mono<Long> userId) {
        return getCart(userId)
                .doOnNext(cart -> cart.removeProduct(productId))
                .then();
    }

    public Mono<Boolean> isEmpty(Mono<Long> userId) {
        return getCart(userId)
                .map(Cart::isEmpty);
    }

    public Mono<Void> clearCart(Mono<Long> userId) {
        return getCart(userId)
                .doOnNext(Cart::clear)
                .then();
    }

    public Mono<Integer> getProductQuantity(Long productId, Mono<Long> userId) {
        return getCart(userId)
                .map(cart -> cart.getProductQuantity(productId));
    }

    public Flux<Long> getProductIdsInCart(Mono<Long> userId) {
        return getCart(userId)
                .flatMapMany(s -> Flux.fromIterable(s.getProductIdsInCart()));
    }

    public Mono<CartDto> getCartView(Mono<Long> userId) {
        return getCart(userId)
                .flatMap(cart ->  {
                    Set<Long> productIdsInCart = cart.getProductIdsInCart();
                    return productCacheService.findAllById(Flux.fromIterable(productIdsInCart)).collectList()
                            .flatMap(products -> {
                                BigDecimal totalPrice = BigDecimal.ZERO;
                                List<CartDto.ProductViewDto> productViewItemList = new ArrayList<>();
                                for (Product product : products) {
                                    Integer quantity = cart.getProductQuantity(product.getId());
                                    totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                                    productViewItemList.add(productMapper.toCartProductViewDto(product, quantity));
                                }
                                
                                final BigDecimal finalTotalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);
                                CartDto cartDto = new CartDto()
                                    .setProducts(productViewItemList)
                                    .setTotalPrice(finalTotalPrice.toString());

                                return paymentApi.getBalance()
                                        .filter(balance -> finalTotalPrice.compareTo(balance) <= 0)
                                        .map(balance -> cartDto.setIsCanBuy(true))
                                        .switchIfEmpty(Mono.just(cartDto.setIsCanBuy(false).setReason("Сумма заказа превышает баланс")))
                                        .onErrorResume(WebClientException.class, error ->
                                                Mono.just(cartDto.setIsCanBuy(false).setReason("Сервис платежей недоступен")))
                                        .defaultIfEmpty(cartDto.setIsCanBuy(false));
                            });
                });
    }
}
