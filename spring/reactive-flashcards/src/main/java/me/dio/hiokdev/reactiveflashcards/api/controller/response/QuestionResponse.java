package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.OffsetDateTime;

public record QuestionResponse(
        @JsonProperty("study_id") String studyId,
        @JsonProperty("asked") String asked,
        @JsonProperty("askedIn") OffsetDateTime askedIn
) {

    @Builder(toBuilder = true)
    public QuestionResponse {}

}