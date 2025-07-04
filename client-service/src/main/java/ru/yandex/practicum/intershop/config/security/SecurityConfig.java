package ru.yandex.practicum.intershop.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import ru.yandex.practicum.intershop.entity.Role;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager authenticationManager,
                                                         ServerLogoutSuccessHandler serverLogoutSuccessHandler) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .formLogin(l -> l.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/product")))
                .authorizeExchange(s -> s
                        .pathMatchers("/product/**", "/css/**", "/scripts/**", "/image/**").permitAll()
                        .pathMatchers("/order/**").authenticated()
                        .pathMatchers("/cart/**").hasRole(Role.USER.name())
                        .anyExchange().authenticated())
                .oauth2Login(withDefaults())
                .logout(logout -> logout
                        .logoutSuccessHandler(serverLogoutSuccessHandler)
                )
                .headers(headerSpec -> headerSpec.referrerPolicy(referrerPolicySpec ->
                        referrerPolicySpec.policy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.ORIGIN_WHEN_CROSS_ORIGIN)))
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .authenticationManager(authenticationManager)
                .build();
    }

    @Bean
    public ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        manager.setPasswordEncoder(passwordEncoder());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}
