package ru.yandex.practicum.intershop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.db.repository.ProductRepository;
import ru.yandex.practicum.intershop.dto.CartDto;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.mapper.ProductMapper;
import ru.yandex.practicum.intershop.service.cart.Cart;
import ru.yandex.practicum.payment.client.api.PaymentApi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CartService {
    private static final String CART_SESSION_KEY = "user_cart";
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PaymentApi paymentApi;

    public CartService(ProductRepository productRepository, ProductMapper productMapper, PaymentApi paymentApi) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.paymentApi = paymentApi;
    }

    private Mono<Cart> getCartFromSession(WebSession session) {
        return Mono.defer(() -> {
            Map<String, Object> attributes = session.getAttributes();
            Cart cart = (Cart) attributes.computeIfAbsent(CART_SESSION_KEY, k -> new Cart());
            return Mono.just(cart);
        });
    }

    public Mono<Void> plusToCart(Long productId, WebSession session) {
        return getCartFromSession(session)
                .doOnNext(cart -> cart.plusProduct(productId))
                .then();
    }

    public Mono<Void> minusFromCart(Long productId, WebSession session) {
        return getCartFromSession(session)
                .doOnNext(cart -> cart.minusProduct(productId))
                .then();
    }

    public Mono<Void> deleteFromCart(Long productId, WebSession session) {
        return getCartFromSession(session)
                .doOnNext(cart -> cart.removeProduct(productId))
                .then();
    }

    public Mono<Boolean> isEmpty(WebSession session) {
        return getCartFromSession(session)
                .map(Cart::isEmpty);
    }

    public Mono<Void> clearCart(WebSession session) {
        return getCartFromSession(session)
                .doOnNext(Cart::clear)
                .then();
    }

    public Mono<Integer> getProductQuantity(Long productId, WebSession session) {
        return getCartFromSession(session)
                .map(cart -> cart.getProductQuantity(productId));
    }

    public Flux<Long> getProductIdsInCart(WebSession session) {
        return getCartFromSession(session)
                .flatMapMany(s -> Flux.fromIterable(s.getProductIdsInCart()));
    }

    public Mono<CartDto> getCartView(WebSession session) {
        return getCartFromSession(session)
                .flatMap(cart ->  {
                    Set<Long> productIdsInCart = cart.getProductIdsInCart();
                    return productRepository.findAllById(productIdsInCart).collectList()
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
