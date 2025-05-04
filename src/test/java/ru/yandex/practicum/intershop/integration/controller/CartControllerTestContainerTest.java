package ru.yandex.practicum.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.integration.AbstractControllerMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class CartControllerTestContainerTest extends AbstractControllerMvcTest {

    MockHttpSession session = new MockHttpSession();

    @Test
    void getCart() throws Exception {
        mockMvc.perform(put("/product/{id}/updateInCart", 1).session(session)
                .param("action", "PLUS")
                .header("Referer", "/product"));
        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("isEmpty", "cart"));
    }

    @Test
    void placeOrder() throws Exception {
        mockMvc.perform(put("/product/{id}/updateInCart", 1).session(session)
                .param("action", "PLUS")
                .header("Referer", "/product"));
        mockMvc.perform(post("/cart/confirm").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/order/*"))
                .andExpect(flash().attributeExists("isNewOrder"));
    }

}