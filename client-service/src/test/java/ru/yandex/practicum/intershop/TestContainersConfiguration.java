package ru.yandex.practicum.intershop;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

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


    @Bean
    @ServiceConnection(name = "redis")
    public RedisContainer redisContainer() {
        return new RedisContainer(DockerImageName.parse("redis:7.4.2-bookworm"));
    }

}
