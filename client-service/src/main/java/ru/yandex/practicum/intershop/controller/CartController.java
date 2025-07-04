package ru.yandex.practicum.intershop.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.config.security.SecurityUser;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.OrderService;
import ru.yandex.practicum.intershop.utils.SecurityUtils;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    public final OrderService orderService;

    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping
    public Mono<Rendering> getCart() {
        Mono<Long> userId = SecurityUtils.getUserId();
        return Mono.zip(cartService.getCartView(userId),
                        cartService.isEmpty(userId), SecurityUtils.getCurrentRole())
                        .map(t -> Rendering.view("cart")
                                .modelAttribute("cart", t.getT1())
                                .modelAttribute("isEmpty", t.getT2())
                                .modelAttribute("role", t.getT3().name()).build());
    }

    @PostMapping("/confirm")
    public Mono<Rendering> placeOrder(@AuthenticationPrincipal SecurityUser securityUser, ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> orderService.placeOrder(securityUser.getUserId())
                        .flatMap(orderId -> {
                            session.getAttributes().put("isNewOrder", true);
                            return Mono.just(Rendering.redirectTo("/order/" + orderId).build());
                        })
                );
    }

}
