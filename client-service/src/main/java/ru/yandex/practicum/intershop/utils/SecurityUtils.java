package ru.yandex.practicum.intershop.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.config.security.SecurityUser;
import ru.yandex.practicum.intershop.entity.Role;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    public static Mono<Role> getCurrentRole() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(auth -> auth.getPrincipal() instanceof SecurityUser)
                .map(auth -> (SecurityUser) auth.getPrincipal())
                .map(SecurityUser::getRole)
                .switchIfEmpty(Mono.just(Role.ANONYMOUS));
    }

    public static Mono<Long> getUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(auth -> auth.getPrincipal() instanceof SecurityUser)
                .map(auth -> (SecurityUser) auth.getPrincipal())
                .map(SecurityUser::getUserId)
                .switchIfEmpty(Mono.empty());
    }
}
