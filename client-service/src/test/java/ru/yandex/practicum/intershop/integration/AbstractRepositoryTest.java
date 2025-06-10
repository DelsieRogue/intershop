package ru.yandex.practicum.intershop.integration;


import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;

@DataR2dbcTest
@ComponentScan("ru.yandex.practicum.intershop.db.dao")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractRepositoryTest extends AbstractTestContainerTest{
}
