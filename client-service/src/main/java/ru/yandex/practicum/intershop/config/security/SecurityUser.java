package ru.yandex.practicum.intershop.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import ru.yandex.practicum.intershop.entity.Role;

import java.util.Collection;

@Getter
public class SecurityUser extends User {
    private final Long userId;
    private final Role role;

    public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Long userId, Role role) {
        super(username, password, authorities);
        this.userId = userId;
        this.role = role;
    }

    public SecurityUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                        boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Long userId, Role role) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.role = role;
    }
}
