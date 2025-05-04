package ru.yandex.practicum.intershop.service.cart;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Cart {
    private final Map<Long, Integer> countProductsMap = new HashMap<>();

    public void plusProduct(Long productId) {
        countProductsMap.put(productId, countProductsMap.getOrDefault(productId, 0) + 1);
    }

    public void minusProduct(Long productId) {
        if (countProductsMap.get(productId) <= 1) {
            countProductsMap.remove(productId);
            return;
        }
        countProductsMap.put(productId, countProductsMap.getOrDefault(productId, 0) - 1);
    }

    public void removeProduct(Long productId) {
        countProductsMap.remove(productId);
    }

    public Set<Long> getProductIdsInCart() {
        return countProductsMap.keySet();
    }

    public Integer getProductQuantity(Long productId) {
        return countProductsMap.get(productId);
    }

    public void clear() {
        countProductsMap.clear();
    }

    public boolean isEmpty() {
        return countProductsMap.isEmpty();
    }
}
