package me.dio.hiokdev.reactiveflashcards.utils.asserts;

import org.springframework.test.web.reactive.server.EntityExchangeResult;

public class SimpleBodyAssertUtils<T> extends AbstractBodyAssertUtils<T> {

    public SimpleBodyAssertUtils(EntityExchangeResult<T> response) {
        super(response);
    }

}
