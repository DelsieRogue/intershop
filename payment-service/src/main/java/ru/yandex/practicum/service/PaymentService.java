package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PaymentService {
    private static final int MAX_ATTEMPTS = 10;
    private final AtomicReference<BigDecimal> balance;

    public PaymentService(@Value("${payment.initial.balance:0}") BigDecimal balance) {
        this.balance = new AtomicReference<>(balance);
    }

    public Mono<BigDecimal> getBalance() {
        return Mono.just(balance.get());
    }

    public Mono<Void> processPayment(Mono<BigDecimal> amount) {
        return amount.flatMap(a -> {
            if (a.compareTo(BigDecimal.ZERO) <= 0) {
                return Mono.error(new IllegalArgumentException("Сумма платежа должна быть положительной!"));
            }
            
            return Mono.fromRunnable(() -> {
                int attempts = 0;
                
                while (attempts < MAX_ATTEMPTS) {
                    BigDecimal current = balance.get();
                    BigDecimal updated = current.subtract(a);
                    
                    if (updated.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("Недостаточно средств!");
                    }
                    if (balance.compareAndSet(current, updated)) {
                        return;
                    }
                    attempts++;
                }
                throw new RuntimeException("Не удалось обработать платеж после нескольких попыток");
            });
        });
    }
}
