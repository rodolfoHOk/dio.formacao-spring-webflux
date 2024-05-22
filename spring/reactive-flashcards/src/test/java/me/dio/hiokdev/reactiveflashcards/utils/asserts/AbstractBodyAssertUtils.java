package me.dio.hiokdev.reactiveflashcards.utils.asserts;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractBodyAssertUtils<T> {

    private final EntityExchangeResult<T> response;

    public T getBody() {
        return response.getResponseBody();
    }

    public AbstractBodyAssertUtils<T> assertBody(final Consumer<T> consumer) {
        consumer.accept(response.getResponseBody());
        return this;
    }

    public AbstractBodyAssertUtils<T> httpStatusIsOk(){
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        return this;
    }

    public AbstractBodyAssertUtils<T> httpStatusIsCreated(){
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
        return this;
    }

    public AbstractBodyAssertUtils<T> httpStatusIsNoContent(){
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);
        return this;
    }

    public AbstractBodyAssertUtils<T> httpStatusIsBadRequest(){
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        return this;
    }

    public AbstractBodyAssertUtils<T> httpStatusIsNotFound(){
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        return this;
    }

    public AbstractBodyAssertUtils<T> httpStatusIsConflict(){
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        return this;
    }

}
