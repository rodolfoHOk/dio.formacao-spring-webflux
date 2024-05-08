package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.core.webclient.DeckApiConfig;
import me.dio.hiokdev.reactiveflashcards.domain.dto.AuthRestDTO;
import me.dio.hiokdev.reactiveflashcards.domain.dto.DeckRestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DeckRestQueryService {

    private final WebClient webClient;
    private final DeckApiConfig deckApiConfig;

    public DeckRestQueryService(final WebClient webClient, final DeckApiConfig deckApiConfig) {
        this.webClient = webClient;
        this.deckApiConfig = deckApiConfig;
    }

    public Flux<DeckRestDTO> getDecks() {
        // todo
        return Flux.empty();
    }

    public Mono<AuthRestDTO> getAuth() {
        // todo
        return Mono.empty();
    }

}
