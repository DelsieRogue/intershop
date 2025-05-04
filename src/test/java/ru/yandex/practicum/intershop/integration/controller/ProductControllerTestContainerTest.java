package ru.yandex.practicum.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.yandex.practicum.intershop.integration.AbstractControllerMvcTest;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class ProductControllerTestContainerTest extends AbstractControllerMvcTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getProducts() throws Exception {
        mockMvc.perform(get("/product")
                        .param("sort", "price")
                        .param("search", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("newProduct", "page", "pageSize", "sort", "search"))
                .andExpect(model().attribute("page", hasProperty("content", hasSize(1))));
    }

    @Test
    void updateInCart() throws Exception {
        mockMvc.perform(put("/product/{id}/updateInCart", 1)
                        .param("action", "PLUS")
                        .header("Referer", "/product"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product"));

    }

    @Test
    void getProduct() throws Exception {
        mockMvc.perform(get("/product/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void createPost() throws Exception {
        mockMvc.perform(multipart("/product")
                        .file("image", new byte[0])
                        .param("price", "1000")
                        .param("title", "Test")
                        .param("description","Test description")
                        .header("Referer", "/product"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product"));
    }
}