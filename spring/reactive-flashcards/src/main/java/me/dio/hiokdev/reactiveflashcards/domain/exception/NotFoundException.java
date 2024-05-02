package me.dio.hiokdev.reactiveflashcards.domain.exception;

public class NotFoundException extends ReactiveFlashCardsException {

    public NotFoundException(final String message) {
        super(message);
    }

}
