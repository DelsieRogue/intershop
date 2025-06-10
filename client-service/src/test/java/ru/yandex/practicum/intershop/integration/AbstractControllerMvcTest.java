package ru.yandex.practicum.intershop.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.payment.client.api.PaymentApi;

@AutoConfigureWebTestClient
public abstract class AbstractControllerMvcTest extends AbstractTestContainerTest {
    @Autowired
    protected WebTestClient webTestClient;
    @MockitoBean
    protected PaymentApi paymentApi;
    static KeycloakContainer keycloak;

    static {
        keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.1.3")
                .withBootstrapAdminDisabled()
                .withRealmImportFile("/keycloak/realm-export.json")
                .withCustomCommand("-- start-dev");
        keycloak.start();
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/master");
    }
}
