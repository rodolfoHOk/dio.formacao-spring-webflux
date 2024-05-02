package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class NotFoundHandler extends AbstractHandleException<NotFoundException> {

    public NotFoundHandler(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final NotFoundException exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.NOT_FOUND);
                    return exception.getMessage();
                })
                .map(message -> buildError(HttpStatus.NOT_FOUND, message))
                .doFirst(() -> log.error("==== NotFoundException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

}
