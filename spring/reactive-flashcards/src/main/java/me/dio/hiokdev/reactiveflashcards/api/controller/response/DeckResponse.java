package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

public record DeckResponse(
        @JsonProperty("id")
        @Schema(description = "identificador do deck", format = "UUID", example = "6633f853d783b72353b83013")
        String id,

        @JsonProperty("name")
        @Schema(description = "Nome do deck", example = "Estudo de inglês")
        String name,

        @JsonProperty("description")
        @Schema(description = "Descrição do deck", example = "Deck de estudo de inglês para iniciantes")
        String description,

        @JsonProperty("cards")
        @Schema(description = "Cards que compõe o deck")
        Set<CardResponse> cards
) {

    @Builder(toBuilder = true)
    public DeckResponse {}

}
