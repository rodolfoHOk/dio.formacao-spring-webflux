package me.dio.hiokdev.reactiveflashcards.domain.document;

import lombok.Builder;

public record Card(
        String front,
        String back
) {

    @Builder(toBuilder = true)
    public Card {}

}
