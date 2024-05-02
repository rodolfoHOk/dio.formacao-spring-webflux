package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ResponseStatusHandler extends AbstractHandleException<ResponseStatusException>{

    public ResponseStatusHandler(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final ResponseStatusException exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.NOT_FOUND);
                    return BaseErrorMessage.GENERIC_NOT_FOUND.getMessage();
                })
                .map(message -> buildError(HttpStatus.NOT_FOUND, message))
                .doFirst(() -> log.error("==== ResponseStatusException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

}
