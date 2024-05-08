package me.dio.hiokdev.reactiveflashcards.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record CardRestDTO(
        @JsonProperty("ask") String ask,
        @JsonProperty("answer") String answer
) {

    @Builder(toBuilder = true)
    public CardRestDTO {}

}
