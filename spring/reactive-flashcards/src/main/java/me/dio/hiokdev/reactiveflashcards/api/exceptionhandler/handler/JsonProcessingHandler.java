package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JsonProcessingHandler extends AbstractHandleException<JsonProcessingException> {

    public JsonProcessingHandler(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final JsonProcessingException exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.BAD_REQUEST);
                    return BaseErrorMessage.GENERIC_BAD_REQUEST.getMessage();
                })
                .map(message -> buildError(HttpStatus.BAD_REQUEST, message))
                .doFirst(() -> log.error("==== JsonProcessingException: Failed to map exception for the request {} ",
                        exchange.getRequest().getPath().value(), exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

}
