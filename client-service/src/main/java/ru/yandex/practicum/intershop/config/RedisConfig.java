package ru.yandex.practicum.intershop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.yandex.practicum.intershop.entity.Product;

@Configuration
public class RedisConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
    @Bean
    public ReactiveRedisTemplate<String, Product> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory, ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<Product> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Product.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Product> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Product> context = builder
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, Product[]> reactiveRedisTemplateArray(
            ReactiveRedisConnectionFactory factory, ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<Product[]> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Product[].class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, Product[]> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Product[]> context = builder
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
} 