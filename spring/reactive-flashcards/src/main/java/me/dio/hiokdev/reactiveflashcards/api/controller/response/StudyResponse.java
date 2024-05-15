package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public record StudyResponse(
        @JsonProperty("id")
        @Schema(description = "Identificador do estudo", example = "663ad679b27ae929b7bcbb8b")
        String id,

        @JsonProperty("userId")
        @Schema(description = "Identificador do usuário", example = "66342b8418c87a1a8a8ffcb0")
        String userId,

        @JsonProperty("studyDeck")
        @Schema(description = "Deck do estudo")
        StudyDeckResponse studyDeck,

        @JsonProperty("questions")
        @Schema(description = "Questões do estudo")
        List<FullQuestionResponse> questions,

        @JsonProperty("completed")
        @Schema(description = "Estudo está completo", example = "false")
        Boolean completed
) {

    @Builder(toBuilder = true)
    public StudyResponse {}

}
