package me.dio.hiokdev.reactiveflashcards.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.DeckRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.DeckResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.DeckMapper;
import me.dio.hiokdev.reactiveflashcards.core.validation.MongoId;
import me.dio.hiokdev.reactiveflashcards.domain.service.DeckService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RestController
@RequestMapping("decks")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class DeckController {

    public final DeckService deckService;
    public final DeckQueryService deckQueryService;
    private final DeckMapper deckMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<DeckResponse> save(@Valid @RequestBody final DeckRequest requestBody) {
        return deckService.save(deckMapper.toDocument(requestBody))
                .doFirst(() -> log.info("==== Saving a deck with follow data {}", requestBody))
                .map(deckMapper::toResponse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "sync")
    public Mono<Void> sync() {
        return deckService.sync()
                .doFirst(() -> log.info("==== Sync decks from External API"));
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<DeckResponse> findById(@PathVariable @Valid @MongoId(message = "{deckController.id}") final String id) {
        return deckQueryService.findById(id)
                .doFirst(() -> log.info("==== Finding a deck with follow id {}", id))
                .map(deckMapper::toResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DeckResponse> findAll() {
        return deckQueryService.findAll()
                .doFirst(() -> log.info("==== Finding all decks"))
                .map(deckMapper::toResponse);
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<DeckResponse> update(
            @PathVariable @Valid @MongoId(message = "{deckController.id}") final String id,
            @RequestBody @Valid final DeckRequest requestBody
    ) {
        return deckService.update(deckMapper.toDocument(requestBody, id))
                .doFirst(() -> log.info("==== Updating a deck with follow info [body: {}, id: {}]", requestBody, id))
                .map(deckMapper::toResponse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "{id}")
    public Mono<Void> delete(@PathVariable @Valid @MongoId(message = "{deckController.id}") final String id) {
        return deckService.delete(id)
                .doFirst(() -> log.info("==== Deleting a user with follow id {}", id));
    }

}
