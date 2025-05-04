package ru.yandex.practicum.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.dto.OrderItemDto;
import ru.yandex.practicum.intershop.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public String getOrder(Model model, @PathVariable Long orderId) {
        OrderDto order = orderService.getOrder(orderId);
        model.addAttribute("order", order);
        return "order";
    }

    @GetMapping()
    public String getOrders(Model model) {
        List<OrderItemDto> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }
}
