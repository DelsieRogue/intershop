package ru.yandex.practicum.intershop.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public abstract class AbstractControllerMvcTest extends AbstractTestContainerTest {
    @Autowired
    protected MockMvc mockMvc;
}
