package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GenericExceptionHandler extends AbstractHandleException<Exception> {

    public GenericExceptionHandler(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final Exception exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
                    return BaseErrorMessage.GENERIC_EXCEPTION.getMessage();
                })
                .map(message -> buildError(HttpStatus.INTERNAL_SERVER_ERROR, message))
                .doFirst(() -> log.error("==== Exception ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

}
