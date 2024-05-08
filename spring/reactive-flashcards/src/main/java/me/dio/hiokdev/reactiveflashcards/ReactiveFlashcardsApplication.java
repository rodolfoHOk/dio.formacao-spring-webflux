package me.dio.hiokdev.reactiveflashcards;

import me.dio.hiokdev.reactiveflashcards.core.retry.RetryConfig;
import me.dio.hiokdev.reactiveflashcards.core.webclient.DeckApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing(dateTimeProviderRef = "dateTimeProvider")
@ConfigurationPropertiesScan(basePackageClasses = {RetryConfig.class, DeckApiConfig.class})
public class ReactiveFlashcardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveFlashcardsApplication.class, args);
    }

}
