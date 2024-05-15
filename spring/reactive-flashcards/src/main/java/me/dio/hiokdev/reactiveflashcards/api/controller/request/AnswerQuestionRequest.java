package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record AnswerQuestionRequest(
        @JsonProperty("answer")
        @Size(min = 1, max = 255)
        @NotBlank
        @Schema(description = "Resposta da pergunta atual", example = "azul")
        String answer
) {

    @Builder(toBuilder = true)
    public AnswerQuestionRequest {}

}
