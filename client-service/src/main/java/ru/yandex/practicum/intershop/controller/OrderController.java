package ru.yandex.practicum.intershop.controller;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.config.security.SecurityUser;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.entity.Role;
import ru.yandex.practicum.intershop.service.OrderService;
import ru.yandex.practicum.intershop.utils.SecurityUtils;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    @PostAuthorize("hasRole('ADMIN') or returnObject?.modelAttributes['order']?.userId == authentication.principal.userId")
    public Mono<Rendering> getOrder(@PathVariable Long orderId) {
        return Mono.zip(orderService.getOrder(orderId), SecurityUtils.getCurrentRole())
                .map(t -> Rendering.view("order")
                        .modelAttribute("order", t.getT1())
                        .modelAttribute("role", t.getT2().name()).build());
    }

    @GetMapping
    public Mono<Rendering> getOrders(@AuthenticationPrincipal SecurityUser securityUser) {
        Flux<OrderItemDto> dtoFlux = AuthorityUtils.authorityListToSet(securityUser.getAuthorities()).contains(Role.ADMIN.getWithPrefix())
                ? orderService.getOrders()
                : orderService.getOrders(securityUser.getUserId());
        return Mono.zip(dtoFlux.collectList(), SecurityUtils.getCurrentRole())
                .map(t -> Rendering.view("orders")
                        .modelAttribute("orders", t.getT1())
                        .modelAttribute("role", t.getT2().name())
                        .build());
    }
}
