package ru.yandex.practicum.intershop.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.CreateProductDto;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ProductService;
import ru.yandex.practicum.intershop.utils.DataBaseRequestUtils;
import ru.yandex.practicum.intershop.utils.SecurityUtils;

@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final CartService cartService;

    public ProductController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping
    public Mono<Rendering> getProducts(
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                   @RequestParam(value = "sort", required = false) String sortField,
                                   @RequestParam(value = "search", required = false) String search, ServerWebExchange exchange) {
        PageRequest pageRequest = DataBaseRequestUtils.productView(page, pageSize, sortField);
        return exchange.getSession().flatMap(session ->  productService.getProducts(session, pageRequest, search))
                .zipWith(SecurityUtils.getCurrentRole())
                .map(t -> Rendering.view("main")
                        .modelAttribute("newProduct", new CreateProductDto())
                        .modelAttribute("page", t.getT1())
                        .modelAttribute("pageSize", pageSize)
                        .modelAttribute("sort", sortField)
                        .modelAttribute("search", search)
                        .modelAttribute("role", t.getT2().name())
                        .build());
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping({"/{productId}/updateInCart"})
    public Mono<Rendering> updateInCart(@PathVariable Long productId,
                                        @RequestBody String action,
                                        ServerWebExchange exchange,
                                        @RequestHeader(value = "Referer", required = false) String referer) {
        return exchange.getSession().flatMap(session -> switch (Action.valueOf(action)) {
            case PLUS -> cartService.plusToCart(productId, session);
            case MINUS -> cartService.minusFromCart(productId, session);
            case DELETE -> cartService.deleteFromCart(productId, session);
        }).then(Mono.just(Rendering.redirectTo(referer).build()));
    }


    @GetMapping("/{productId}")
    public Mono<Rendering> getProduct(@PathVariable Long productId, WebSession session) {
        return Mono.zip(productService.getProduct(session, productId), SecurityUtils.getCurrentRole())
                        .map(t -> Rendering.view("item")
                                .modelAttribute("product", t.getT1())
                                .modelAttribute("role", t.getT2().name()).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Mono<Rendering> createProduct(@ModelAttribute("newProduct") CreateProductDto productDto,
                             @RequestHeader(value = "Referer", required = false) String referer,
                             @RequestPart(value = "image", required = false) Mono<FilePart> image) {
        return productService.createProduct(productDto, image).then(Mono.just(Rendering.redirectTo(referer).build()));
    }
}
