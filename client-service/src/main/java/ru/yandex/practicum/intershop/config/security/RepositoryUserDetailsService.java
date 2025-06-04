package ru.yandex.practicum.intershop.config.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.db.repository.UserRepository;

import java.util.List;

@Component
public class RepositoryUserDetailsService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    public RepositoryUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Пользователь не найден")))
                .map(user -> new User(user.getUsername(), user.getSecret(),
                        List.of(new SimpleGrantedAuthority(user.getRole().name()))));
    }
}
