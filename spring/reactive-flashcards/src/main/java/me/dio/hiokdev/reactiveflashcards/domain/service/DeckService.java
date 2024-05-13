package me.dio.hiokdev.reactiveflashcards.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.DeckDomainMapper;
import me.dio.hiokdev.reactiveflashcards.domain.repository.DeckRepository;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckRestQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final DeckRestQueryService deckRestQueryService;
    private final DeckDomainMapper deckDomainMapper;

    public Mono<DeckDocument> save(final DeckDocument deckDocument) {
        return deckRepository.save(deckDocument)
                .doFirst(() -> log.info("==== Try to save a follow deck {}", deckDocument));
    }

    public Mono<Void> sync() {
        return Mono.empty()
                .onTerminateDetach()
                .doOnSuccess(object -> backgroundSync())
                .then();
    }

    private void backgroundSync() {
        deckRestQueryService.getDecks()
                .map(deckDomainMapper::toDocument)
                .doFirst(() -> log.info("==== Try to get decks from external API"))
                .flatMap(deckRepository::save)
                .then()
                .subscribe();
    }

}
