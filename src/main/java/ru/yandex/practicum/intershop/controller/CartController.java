package ru.yandex.practicum.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.OrderService;

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
    public Mono<Rendering> getCart(WebSession session) {
        return cartService.getCartView(session).zipWith(cartService.isEmpty(session))
                        .map(t -> Rendering.view("cart")
                                .modelAttribute("cart", t.getT1())
                                .modelAttribute("isEmpty", t.getT2()).build());
    }

    @PostMapping("/confirm")
    public Mono<Rendering> placeOrder(ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> orderService.placeOrder(session)
                        .flatMap(orderId -> {
                            session.getAttributes().put("isNewOrder", true);
                            return Mono.just(Rendering.redirectTo("/order/" + orderId).build());
                        })
                );
    }

}
