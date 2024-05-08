package me.dio.hiokdev.reactiveflashcards.core.webclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("deck-api")
public record DeckApiConfig(
        String baseUrl,
        String authResource,
        String decksResource
) {
}