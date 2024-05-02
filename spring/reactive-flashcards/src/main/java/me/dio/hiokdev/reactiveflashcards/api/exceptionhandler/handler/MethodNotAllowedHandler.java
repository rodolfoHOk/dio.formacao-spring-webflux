package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MethodNotAllowedHandler extends AbstractHandleException<MethodNotAllowedException> {

    public MethodNotAllowedHandler(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final MethodNotAllowedException exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.METHOD_NOT_ALLOWED);
                    return BaseErrorMessage.GENERIC_METHOD_NOT_ALLOWED
                            .params(exchange.getRequest().getMethod().name()).getMessage();
                })
                .map(message -> buildError(HttpStatus.METHOD_NOT_ALLOWED, message))
                .doFirst(() -> log.error("==== MethodNotAllowedException: Method [{}] is not allowed at [{}]",
                        exchange.getRequest().getMethod(), exchange.getRequest().getPath().value(), exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

}
