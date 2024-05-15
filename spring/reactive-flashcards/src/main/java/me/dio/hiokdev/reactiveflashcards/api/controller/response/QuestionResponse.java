package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;

public record QuestionResponse(
        @JsonProperty("study_id")
        @Schema(description = "Identificador do estudo", example = "663ad679b27ae929b7bcbb8b")
        String studyId,

        @JsonProperty("asked")
        @Schema(description = "Pergunta feita", example = "blue")
        String asked,

        @JsonProperty("askedIn")
        @Schema(description = "Momento em que a pergunta foi gerada", format = "datetime", example = "2024-05-15T14:30:00Z")
        OffsetDateTime askedIn
) {

    @Builder(toBuilder = true)
    public QuestionResponse {}

}
