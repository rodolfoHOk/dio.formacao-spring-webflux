package me.dio.hiokdev.reactiveflashcards.utils.asserts;

import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.List;

public class CollectionBodyAssertUtils<T> extends AbstractBodyAssertUtils<List<T>> {

    public CollectionBodyAssertUtils(EntityExchangeResult<List<T>> response) {
        super(response);
    }

}
