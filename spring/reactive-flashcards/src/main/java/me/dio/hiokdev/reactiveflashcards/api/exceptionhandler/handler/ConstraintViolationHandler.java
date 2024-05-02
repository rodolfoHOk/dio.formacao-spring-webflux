package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ConstraintViolationHandler extends AbstractHandleException<ConstraintViolationException>{

    public ConstraintViolationHandler(final ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final ConstraintViolationException exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.BAD_REQUEST);
                    return BaseErrorMessage.GENERIC_BAD_REQUEST.getMessage();
                })
                .map(message -> buildError(HttpStatus.BAD_REQUEST, message))
                .flatMap(problemResponse -> buildParamErrorMessage(problemResponse, exception))
                .doFirst(() -> log.error("==== ConstraintViolationException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
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

}
