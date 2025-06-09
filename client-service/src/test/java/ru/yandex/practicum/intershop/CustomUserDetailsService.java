package ru.yandex.practicum.intershop;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.intershop.config.security.SecurityUser;
import ru.yandex.practicum.intershop.entity.Role;

import java.util.List;

@Primary
@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("test_admin".equals(username)) {
            return new SecurityUser("test_admin", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_" + Role.ADMIN.name())), 1L, Role.ADMIN);
        }
        if ("test_user".equals(username)) {
            return new SecurityUser("test_user", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_" + Role.USER.name())), 1L, Role.USER);
        }
        throw new UsernameNotFoundException("User not found");
    }
}
