package ru.yandex.practicum.intershop.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.db.repository.UserRepository;
import ru.yandex.practicum.intershop.entity.Role;
import ru.yandex.practicum.intershop.entity.User;
import ru.yandex.practicum.intershop.integration.AbstractRepositoryTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByUsername() {
        Mono<User> result = userRepository.findByUsername("terminator");
        StepVerifier.create(result)
                .assertNext((s) -> assertEquals(Role.ADMIN, s.getRole()))
                .verifyComplete();
    }

}
