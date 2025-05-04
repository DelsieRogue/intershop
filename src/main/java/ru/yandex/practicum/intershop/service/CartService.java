package ru.yandex.practicum.intershop.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.intershop.dto.CartDto;
import ru.yandex.practicum.intershop.entity.Product;
import ru.yandex.practicum.intershop.mapper.ProductMapper;
import ru.yandex.practicum.intershop.repository.ProductRepository;
import ru.yandex.practicum.intershop.service.cart.Cart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CartService {
    private final Cart cart;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public CartService(Cart cart, ProductRepository productRepository, ProductMapper productMapper) {
        this.cart = cart;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public void plusToCart(Long productId) {
        cart.plusProduct(productId);
    }

    public void minusFromCart(Long productId) {
        cart.minusProduct(productId);
    }

    public void deleteFromCart(Long productId) {
        cart.removeProduct(productId);
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }
    public void clearCart() {
        cart.clear();
    }

    public Integer getProductQuantity(Long productId) {
        return cart.getProductQuantity(productId);
    }

    public Set<Long> getProductIdsInCart() {
        return cart.getProductIdsInCart();
    }

    public CartDto getCartView() {
        Set<Long> productIdsInCart= cart.getProductIdsInCart();
        List<Product> products = productRepository.findAllById(productIdsInCart);
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<CartDto.ProductViewDto> productViewItemList = new ArrayList<>();
        for (Product product : products) {
            Integer quantity = cart.getProductQuantity(product.getId());
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            productViewItemList.add(productMapper.toCartProductViewDto(product, quantity));
        }
        return new CartDto().setProducts(productViewItemList).setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP).toString());
    }
}
