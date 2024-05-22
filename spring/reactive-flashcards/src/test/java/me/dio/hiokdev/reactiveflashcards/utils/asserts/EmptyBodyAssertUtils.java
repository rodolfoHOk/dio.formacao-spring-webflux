package me.dio.hiokdev.reactiveflashcards.utils.asserts;

import org.springframework.test.web.reactive.server.EntityExchangeResult;

public class EmptyBodyAssertUtils extends AbstractBodyAssertUtils<Void> {

    public EmptyBodyAssertUtils(EntityExchangeResult<Void> response) {
        super(response);
    }

}
