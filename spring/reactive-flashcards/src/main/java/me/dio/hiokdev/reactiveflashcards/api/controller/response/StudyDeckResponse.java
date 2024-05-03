package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Set;

public record StudyDeckResponse(
        @JsonProperty("deckId") String deckId,
        @JsonProperty("cards") Set<CardResponse> cards
) {

    @Builder(toBuilder = true)
    public StudyDeckResponse {}

}
