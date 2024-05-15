package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import me.dio.hiokdev.reactiveflashcards.core.validation.MongoId;

public record StudyRequest(
        @JsonProperty("userId")
        @MongoId
        @Schema(description = "Identificador do usuário", example = "66342b8418c87a1a8a8ffcb0")
        String userId,

        @JsonProperty("deckId")
        @MongoId
        @Schema(description = "Identificador do deck", example = "6633f853d783b72353b83013")
        String deckId
) {

    @Builder(toBuilder = true)
    public StudyRequest {}

}
