package me.dio.hiokdev.reactiveflashcards.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthRestDTO(
        @JsonProperty("token") String token,
        @JsonProperty("expiresIn") Long expiresIn
) {
}
