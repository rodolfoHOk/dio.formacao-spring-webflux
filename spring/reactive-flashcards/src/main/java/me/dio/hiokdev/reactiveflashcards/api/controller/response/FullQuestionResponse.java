package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

public record FullQuestionResponse(
        @JsonProperty("asked") String asked,
        @JsonProperty("askedIn") OffsetDateTime askedIn,
        @JsonProperty("answered") String answered,
        @JsonProperty("answeredIn") OffsetDateTime answeredIn
) {

    @Builder(toBuilder = true)
    public FullQuestionResponse {}

}
