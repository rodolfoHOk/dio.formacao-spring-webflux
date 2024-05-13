package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.core.webclient.DeckApiConfig;
import me.dio.hiokdev.reactiveflashcards.domain.dto.AuthRestDTO;
import me.dio.hiokdev.reactiveflashcards.domain.dto.DeckRestDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
public class DeckRestQueryService {

    private final WebClient webClient;
    private final DeckApiConfig deckApiConfig;
    private final Mono<AuthRestDTO> authCache;

    public DeckRestQueryService(final WebClient webClient, final DeckApiConfig deckApiConfig) {
        this.webClient = webClient;
        this.deckApiConfig = deckApiConfig;
        this.authCache = Mono.from(getAuth()).cache(
                auth -> Duration.ofSeconds(auth.expiresIn() - 5),
                throwable -> Duration.ZERO,
                () -> Duration.ZERO
        );
    }

    public Flux<DeckRestDTO> getDecks() {
        return authCache.flatMapMany(cache -> doGetDecks(cache.token()));
    }

    private Flux<DeckRestDTO> doGetDecks(final String token) {
        return webClient.get()
                .uri(deckApiConfig.getDecksUri())
                .header("token", token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToFlux(DeckRestDTO.class);
    }

    private Mono<AuthRestDTO> getAuth() {
        return webClient.post()
                .uri(deckApiConfig.authResource())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AuthRestDTO.class);
    }

}
