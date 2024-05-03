package me.dio.hiokdev.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import me.dio.hiokdev.reactiveflashcards.core.validation.MongoId;

public record StudyRequest(
        @JsonProperty("userId") @MongoId String userId,
        @JsonProperty("deckId") @MongoId String deckId
) {

    @Builder(toBuilder = true)
    public StudyRequest {}

}
