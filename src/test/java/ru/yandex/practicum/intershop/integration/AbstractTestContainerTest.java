package ru.yandex.practicum.intershop.integration;


import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.intershop.TestContainersConfiguration;

@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
public abstract class AbstractTestContainerTest {

}
