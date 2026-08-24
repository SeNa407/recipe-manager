package com.cookbook.app.recipe_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient universalWebClient() {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(codecs ->
                        codecs.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}
