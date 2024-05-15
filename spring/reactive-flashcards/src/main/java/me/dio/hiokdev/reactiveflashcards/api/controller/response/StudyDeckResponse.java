package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

public record StudyDeckResponse(
        @JsonProperty("deckId")
        @Schema(description = "Identificador do deck", example = "6633f853d783b72353b83013")
        String deckId,

        @JsonProperty("cards")
        @Schema(description = "Cards que compõe o deck")
        Set<CardResponse> cards
) {

    @Builder(toBuilder = true)
    public StudyDeckResponse {}

}
