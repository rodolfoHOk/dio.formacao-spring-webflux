package me.dio.hiokdev.reactiveflashcards.domain.exception;

public class EmailAlreadyUsedException extends ReactiveFlashCardsException {

    public EmailAlreadyUsedException(String message) {
        super(message);
    }

}
