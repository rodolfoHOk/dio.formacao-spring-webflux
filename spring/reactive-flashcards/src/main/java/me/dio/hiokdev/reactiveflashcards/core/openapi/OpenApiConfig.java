package me.dio.hiokdev.reactiveflashcards.core.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Reactive FlashCards", description = "API reativa de estudo de flash cards"),
        servers = {
                @Server(url = "http://localhost:8080/reactive-flashcards", description = "local")
        }
)
public class OpenApiConfig {
}
