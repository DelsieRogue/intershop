package ru.yandex.practicum.intershop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfiguration implements WebFluxConfigurer {
    private final String serverPath;
    private final String clientPath;

    public WebConfiguration(@Value("${image.folder.server.path}") String serverPath,
                            @Value("${image.folder.client.path}") String clientPath) {
        this.serverPath = serverPath;
        this.clientPath = clientPath;
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(clientPath + "**")
                .addResourceLocations("file:" + serverPath);
    }
}

