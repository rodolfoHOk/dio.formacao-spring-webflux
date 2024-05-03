package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.ConstraintViolationHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.EmailAlreadyUsedHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.GenericExceptionHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.JsonProcessingHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.MethodNotAllowedHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.NotFoundHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.ReactiveFlashCardsHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.ResponseStatusHandler;
import me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler.WebExchangeBindHandler;
import me.dio.hiokdev.reactiveflashcards.domain.exception.EmailAlreadyUsedException;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.exception.ReactiveFlashCardsException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
public class ApiExceptionHandler implements WebExceptionHandler {

    private final EmailAlreadyUsedHandler emailAlreadyUsedHandler;
    private final NotFoundHandler notFoundHandler;
    private final ConstraintViolationHandler constraintViolationHandler;
    private final WebExchangeBindHandler webExchangeBindHandler;
    private final MethodNotAllowedHandler methodNotAllowHandler;
    private final ResponseStatusHandler responseStatusHandler;
    private final ReactiveFlashCardsHandler reactiveFlashCardsHandler;
    private final GenericExceptionHandler genericExceptionHandler;
    private final JsonProcessingHandler jsonProcessingHandler;

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
        return Mono.error(ex)
                .onErrorResume(EmailAlreadyUsedException.class, e -> emailAlreadyUsedHandler
                        .handlerException(exchange, e))
                .onErrorResume(NotFoundException.class, e -> notFoundHandler.handlerException(exchange, e))
                .onErrorResume(ConstraintViolationException.class, e -> constraintViolationHandler
                        .handlerException(exchange, e))
                .onErrorResume(WebExchangeBindException.class, e -> webExchangeBindHandler
                        .handlerException(exchange, e))
                .onErrorResume(MethodNotAllowedException.class, e -> methodNotAllowHandler
                        .handlerException(exchange, e))
                .onErrorResume(ResponseStatusException.class, e -> responseStatusHandler.handlerException(exchange, e))
                .onErrorResume(ReactiveFlashCardsException.class, e -> reactiveFlashCardsHandler
                        .handlerException(exchange, e))
                .onErrorResume(Exception.class, e -> genericExceptionHandler.handlerException(exchange, e))
                .onErrorResume(JsonProcessingException.class, e -> jsonProcessingHandler.handlerException(exchange, e))
                .then();
    }

}
