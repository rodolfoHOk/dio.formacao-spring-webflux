package me.dio.hiokdev.reactiveflashcards.domain.exception;

public class ReactiveFlashCardsException extends RuntimeException {

    public ReactiveFlashCardsException(final String message) {
        super(message);
    }

    public ReactiveFlashCardsException(String message, Throwable cause) {
        super(message, cause);
    }

}
