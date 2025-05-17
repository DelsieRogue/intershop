package ru.yandex.practicum.intershop;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> container() {
        return new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("junit")
                .withPassword("junit");
    }
}
