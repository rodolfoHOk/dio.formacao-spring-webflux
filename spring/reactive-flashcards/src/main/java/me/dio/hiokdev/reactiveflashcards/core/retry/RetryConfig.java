package me.dio.hiokdev.reactiveflashcards.core.retry;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("reactive-flashcards.retry-config")
public record RetryConfig(
        Long maxRetries,
        Long minDuration
) {

    public Duration minDurationSeconds() {
        return Duration.ofSeconds(minDuration);
    }

}
