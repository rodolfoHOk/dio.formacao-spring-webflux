package me.dio.hiokdev.reactiveflashcards.api.controller.response;

import lombok.Builder;

public record ErrorFieldResponse(
    String name,
    String message
) {

    @Builder(toBuilder = true)
    public ErrorFieldResponse {}

}
