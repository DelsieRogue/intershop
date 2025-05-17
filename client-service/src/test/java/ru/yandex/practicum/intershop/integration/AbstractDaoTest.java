package ru.yandex.practicum.intershop.integration;


import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AbstractDaoTest extends AbstractTestContainerTest {
}
