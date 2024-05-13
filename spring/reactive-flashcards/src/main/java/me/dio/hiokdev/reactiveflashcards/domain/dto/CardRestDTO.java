package me.dio.hiokdev.reactiveflashcards.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CardRestDTO(
        @JsonProperty("ask") String ask,
        @JsonProperty("answer") String answer
) {
}
