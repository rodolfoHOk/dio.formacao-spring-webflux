package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;

public record FullQuestionResponse(
        @JsonProperty("asked")
        @Schema(description = "Pergunta feita", example = "blue")
        String asked,

        @JsonProperty("askedIn")
        @Schema(description = "Momento em que a pergunta foi gerada", format = "datetime", example = "2024-05-15T14:30:00Z")
        OffsetDateTime askedIn,

        @JsonProperty("answered")
        @Schema(description = "Resposta fornecida", example = "azul")
        String answered,

        @JsonProperty("answeredIn")
        @Schema(description = "Momento em que a pergunta foi respondida", format = "datetime", example = "2024-05-15T14:30:00Z")
        OffsetDateTime answeredIn
) {

    @Builder(toBuilder = true)
    public FullQuestionResponse {}

}
