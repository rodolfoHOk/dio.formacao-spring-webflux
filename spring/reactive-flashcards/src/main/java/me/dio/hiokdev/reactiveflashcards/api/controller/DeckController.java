package me.dio.hiokdev.reactiveflashcards.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.DeckRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.DeckResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.DeckMapper;
import me.dio.hiokdev.reactiveflashcards.domain.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RestController
@RequestMapping("decks")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class DeckController {

    public final DeckService deckService;
    private final DeckMapper deckMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<DeckResponse> save(@Valid @RequestBody final DeckRequest requestBody) {
        return deckService.save(deckMapper.toDocument(requestBody))
                .doFirst(() -> log.info("==== Saving a deck with follow data {}", requestBody))
                .map(deckMapper::toResponse);
    }

}
