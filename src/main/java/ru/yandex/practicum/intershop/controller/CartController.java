package ru.yandex.practicum.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.practicum.intershop.dto.CartDto;
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
    public String getCart(Model model) {
        CartDto cartView = cartService.getCartView();
        model.addAttribute("isEmpty", cartService.isEmpty());
        model.addAttribute("cart", cartView);
        return "cart";
    }

    @PostMapping("/confirm")
    public String placeOrder(RedirectAttributes attributes) {
        Long orderId = orderService.placeOrder();
        attributes.addFlashAttribute("isNewOrder", true);
        return "redirect:/order/" + orderId;
    }

}
