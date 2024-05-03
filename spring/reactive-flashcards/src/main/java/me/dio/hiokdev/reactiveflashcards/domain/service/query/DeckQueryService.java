package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.repository.DeckRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeckQueryService {

    private final DeckRepository deckRepository;

    public Mono<DeckDocument> findById(final String id) {
        return deckRepository.findById(id)
                .doFirst(() -> log.info("==== Try to find deck with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(BaseErrorMessage
                        .DECK_NOT_FOUND.params(id).getMessage()))));
    }

    public Flux<DeckDocument> findAll() {
        return deckRepository.findAll()
                .doFirst(() -> log.info("==== Try to get all decks"));
    }

}
