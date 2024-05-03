package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.exception.DeckInStudyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DeckInStudyHandler extends AbstractHandleException<DeckInStudyException> {

    public DeckInStudyHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Mono<Void> handlerException(ServerWebExchange exchange, DeckInStudyException exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.CONFLICT);
                    return exception.getMessage();
                })
                .map(message -> buildError(HttpStatus.CONFLICT, message))
                .doFirst(() -> log.error("==== DeckInStudyException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

}
