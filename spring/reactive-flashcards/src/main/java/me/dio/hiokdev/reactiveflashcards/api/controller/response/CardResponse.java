package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public record CardResponse(
        @JsonProperty("front")
        @Schema(description = "Pergunta do card", example = "blue")
        String front,

        @JsonProperty("back")
        @Schema(description = "Resposta do card", example = "azul")
        String back
) {

    @Builder(toBuilder = true)
    public CardResponse {}

}
