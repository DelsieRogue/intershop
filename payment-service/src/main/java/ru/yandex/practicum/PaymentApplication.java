package ru.yandex.practicum;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PaymentApplication {

	@Bean
	public OpenAPI paymentOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Payment Service API")
						.description("API для платежного сервиса")
						.version("v1"));
	}

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

}
