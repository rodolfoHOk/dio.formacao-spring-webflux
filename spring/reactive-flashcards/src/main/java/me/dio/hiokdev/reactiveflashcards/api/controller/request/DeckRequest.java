package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

public record DeckRequest(
        @JsonProperty("name")
        @NotBlank
        @Size(min = 1, max = 255)
        @Schema(description = "Nome do deck", example = "Estudo de inglês")
        String name,

        @JsonProperty("description")
        @NotBlank
        @Size(min = 1, max = 255)
        @Schema(description = "Descrição do deck", example = "Deck de estudo de inglês para iniciantes")
        String description,

        @JsonProperty("cards")
        @Valid
        @NotNull
        @Size(min = 3)
        @Schema(description = "Cards que compõe o deck")
        Set<CardRequest> cards
) {

    @Builder(toBuilder = true)
    public DeckRequest {}

}
