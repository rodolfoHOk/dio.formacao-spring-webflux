package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.exception.ReactiveFlashCardsException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReactiveFlashCardsHandler extends AbstractHandleException<ReactiveFlashCardsException> {

    public ReactiveFlashCardsHandler(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final ReactiveFlashCardsException exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
                    return BaseErrorMessage.GENERIC_EXCEPTION.getMessage();
                })
                .map(message -> buildError(HttpStatus.INTERNAL_SERVER_ERROR, message))
                .doFirst(() -> log.error("==== ReactiveFlashCardsException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

}
