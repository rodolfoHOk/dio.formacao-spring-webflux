package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.exception.ReactiveFlashCardsException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
public class ApiExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
        return Mono.error(ex)
                .onErrorResume(NotFoundException.class, e -> handleNotFoundException(exchange, e))
                .onErrorResume(ResponseStatusException.class, e -> handleResponseStatusException(exchange, e))
                .onErrorResume(ConstraintViolationException.class, e -> handleConstraintViolationException(exchange, e))
                .onErrorResume(WebExchangeBindException.class, e -> handleWebExchangeBindException(exchange, e))
                .onErrorResume(MethodNotAllowedException.class, e -> handleMethodNotAllowedException(exchange, e))
                .onErrorResume(ReactiveFlashCardsException.class, e -> handleReactiveFlashCardsException(exchange, e))
                .onErrorResume(Exception.class, e -> handleException(exchange, e))
                .onErrorResume(JsonProcessingException.class, e -> handleJsonProcessingException(exchange, e))
                .then();
    }

    private Mono<Void> handleNotFoundException(
            final ServerWebExchange exchange,
            final NotFoundException exception
    ) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.NOT_FOUND);
                    return exception.getMessage();
                })
                .map(message -> buildError(HttpStatus.NOT_FOUND, message))
                .doFirst(() -> log.error("==== NotFoundException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleResponseStatusException(
            final ServerWebExchange exchange,
            final ResponseStatusException exception
    ) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.NOT_FOUND);
                    return BaseErrorMessage.GENERIC_NOT_FOUND.getMessage();
                })
                .map(message -> buildError(HttpStatus.NOT_FOUND, message))
                .doFirst(() -> log.error("==== ResponseStatusException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleConstraintViolationException(
            final ServerWebExchange exchange,
            final ConstraintViolationException exception
    ) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.BAD_REQUEST);
                    return BaseErrorMessage.GENERIC_BAD_REQUEST.getMessage();
                })
                .map(message -> buildError(HttpStatus.BAD_REQUEST, message))
                .flatMap(problemResponse -> buildParamErrorMessage(problemResponse, exception))
                .doFirst(() -> log.error("==== ConstraintViolationException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleWebExchangeBindException(
            final ServerWebExchange exchange,
            final WebExchangeBindException exception
    ) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.BAD_REQUEST);
                    return BaseErrorMessage.GENERIC_BAD_REQUEST.getMessage();
                })
                .map(message -> buildError(HttpStatus.BAD_REQUEST, message))
                .flatMap(problemResponse -> buildParamErrorMessage(problemResponse, exception))
                .doFirst(() -> log.error("==== WebExchangeBindException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleMethodNotAllowedException(
            final ServerWebExchange exchange,
            final MethodNotAllowedException exception
    ) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.METHOD_NOT_ALLOWED);
                    return BaseErrorMessage.GENERIC_METHOD_NOT_ALLOWED.getMessage();
                })
                .map(message -> buildError(HttpStatus.METHOD_NOT_ALLOWED, message))
                .doFirst(() -> log.error("==== MethodNotAllowedException: Method [{}] is not allowed at [{}]",
                        exchange.getRequest().getMethod(), exchange.getRequest().getPath().value(), exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleReactiveFlashCardsException(
            final ServerWebExchange exchange,
            final ReactiveFlashCardsException exception
    ) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
                    return BaseErrorMessage.GENERIC_EXCEPTION.getMessage();
                })
                .map(message -> buildError(HttpStatus.INTERNAL_SERVER_ERROR, message))
                .doFirst(() -> log.error("==== ReactiveFlashCardsException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleException(final ServerWebExchange exchange, final Exception exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.INTERNAL_SERVER_ERROR);
                    return BaseErrorMessage.GENERIC_EXCEPTION.getMessage();
                })
                .map(message -> buildError(HttpStatus.INTERNAL_SERVER_ERROR, message))
                .doFirst(() -> log.error("==== Exception ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleJsonProcessingException(
            final ServerWebExchange exchange,
            final JsonProcessingException exception
    ) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.BAD_REQUEST);
                    return BaseErrorMessage.GENERIC_BAD_REQUEST.getMessage();
                })
                .map(message -> buildError(HttpStatus.BAD_REQUEST, message))
                .doFirst(() -> log.error("==== JsonProcessingException: Failed to map exception for the request {} ",
                        exchange.getRequest().getPath().value(), exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private void prepareExchange(final ServerWebExchange exchange, final HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    }

    private ProblemResponse buildError(final HttpStatus status, final String errorDescription) {
        return ProblemResponse.builder()
                .status(status.value())
                .errorDescription(errorDescription)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    private Mono<ProblemResponse> buildParamErrorMessage(
            final ProblemResponse problemResponse,
            final ConstraintViolationException exception
    ) {
        return Flux.fromIterable(exception.getConstraintViolations())
                .map(constraintViolation -> ErrorFieldResponse.builder()
                        .name(((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().toString())
                        .message(constraintViolation.getMessage())
                        .build()
                )
                .collectList()
                .map(errorFieldResponseList -> problemResponse.toBuilder()
                        .fields(errorFieldResponseList)
                        .build());
    }

    private Mono<ProblemResponse> buildParamErrorMessage(
            final ProblemResponse problemResponse,
            final WebExchangeBindException exception
    ) {
        return Flux.fromIterable(exception.getAllErrors())
                .map(objectError -> ErrorFieldResponse.builder()
                        .name(objectError.getObjectName())
                        .message(messageSource.getMessage(objectError, LocaleContextHolder.getLocale()))
                        .build()
                )
                .collectList()
                .map(errorFieldResponseList -> problemResponse.toBuilder()
                        .fields(errorFieldResponseList)
                        .build());
    }

    private Mono<Void> writeResponse(final ServerWebExchange exchange, final ProblemResponse problemResponse) {
        return exchange.getResponse()
                .writeWith(Mono.fromCallable(() -> new DefaultDataBufferFactory()
                        .wrap(objectMapper.writeValueAsBytes(problemResponse))));
    }

}
