package ru.yandex.practicum.intershop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.payment.client.api.PaymentApi;
import ru.yandex.practicum.payment.client.invoker.ApiClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentApi paymentApi(@Value("${api.client.payment-service.url}") String url) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(url);
        return new PaymentApi(apiClient);
    }
}
