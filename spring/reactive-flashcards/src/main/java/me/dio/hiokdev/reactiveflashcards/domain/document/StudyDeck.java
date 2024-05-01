package me.dio.hiokdev.reactiveflashcards.domain.document;

import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

public record StudyDeck(
        @Field("deck_id") String deckId,
        Set<StudyCard> cards
) {

    @Builder(toBuilder = true)
    public StudyDeck {}

}
