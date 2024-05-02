package me.dio.hiokdev.reactiveflashcards.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.repository.DeckRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;

    public Mono<DeckDocument> save(final DeckDocument deckDocument) {
        return deckRepository.save(deckDocument)
                .doFirst(() -> log.info("==== Try to save a follow deck {}", deckDocument));
    }

}
