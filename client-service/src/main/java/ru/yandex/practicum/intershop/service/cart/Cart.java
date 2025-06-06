package ru.yandex.practicum.intershop.service.cart;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Cart {
    private final ConcurrentMap<Long, Integer> countProductsMap = new ConcurrentHashMap<>();

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
        return countProductsMap.getOrDefault(productId, 0);
    }

    public void clear() {
        countProductsMap.clear();
    }

    public boolean isEmpty() {
        return countProductsMap.isEmpty();
    }
}
