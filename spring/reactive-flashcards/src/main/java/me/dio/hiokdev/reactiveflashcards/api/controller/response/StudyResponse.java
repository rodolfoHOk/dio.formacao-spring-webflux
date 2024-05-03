package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

public record StudyResponse(
        @JsonProperty("id") String id,
        @JsonProperty("userId") String userId,
        @JsonProperty("studyDeck") StudyDeckResponse studyDeck,
        @JsonProperty("questions") List<FullQuestionResponse> questions,
        @JsonProperty("completed") Boolean completed
) {

    @Builder(toBuilder = true)
    public StudyResponse {}

}
