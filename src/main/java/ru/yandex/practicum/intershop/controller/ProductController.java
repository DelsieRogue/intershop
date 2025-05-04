package ru.yandex.practicum.intershop.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.CreateProductDto;
import ru.yandex.practicum.intershop.dto.ProductItemDto;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ProductService;
import ru.yandex.practicum.intershop.utils.DataBaseRequestUtils;

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
    public String getProducts(Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                              @RequestParam(value = "sort", required = false) String sortField,
                              @RequestParam(value = "search", required = false) String search) {
        PageRequest pageRequest = DataBaseRequestUtils.productView(page, pageSize, sortField);
        Page<ProductItemDto> products = productService.getProducts(pageRequest, search);
        model.addAttribute("newProduct", new CreateProductDto());
        model.addAttribute("page", products);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sort", sortField);
        model.addAttribute("search", search);
        return "main";
    }

    @PutMapping({"/{productId}/updateInCart"})
    public String updateInCart(@PathVariable Long productId,
                               @RequestParam Action action,
                               @RequestHeader(value = "Referer", required = false) String referer) {
        switch (action) {
            case PLUS -> cartService.plusToCart(productId);
            case MINUS -> cartService.minusFromCart(productId);
            case DELETE -> cartService.deleteFromCart(productId);
        }
        return "redirect:" + referer;
    }


    @GetMapping("/{productId}")
    public String getProduct(Model model, @PathVariable Long productId) {
        ProductItemDto productDto = productService.getProduct(productId);
        model.addAttribute("product", productDto);
        return "item";
    }

    @PostMapping
    public String createProduct(@ModelAttribute("newProduct") CreateProductDto productDto,
                             @RequestHeader(value = "Referer", required = false) String referer,
                             @RequestPart(value = "image", required = false) MultipartFile image) {
        productService.createProduct(productDto, image);
        return "redirect:" + referer;
    }
}
