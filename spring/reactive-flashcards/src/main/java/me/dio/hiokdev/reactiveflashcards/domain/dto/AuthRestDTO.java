package me.dio.hiokdev.reactiveflashcards.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record AuthRestDTO(
        @JsonProperty("token") String token,
        @JsonProperty("expiresIn") Long expiresIn
) {

    @Builder(toBuilder = true)
    public AuthRestDTO {}

}
