package br.com.contis.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${shopify.api.url}")
    private String shopifyUrl;

    @Value("${shopify.api.access-token}")
    private String accessToken;

    @Bean
    public WebClient shopifyWebClient() {
        return WebClient.builder()
                .baseUrl(shopifyUrl)
                .defaultHeader("X-Shopify-Access-Token", accessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}