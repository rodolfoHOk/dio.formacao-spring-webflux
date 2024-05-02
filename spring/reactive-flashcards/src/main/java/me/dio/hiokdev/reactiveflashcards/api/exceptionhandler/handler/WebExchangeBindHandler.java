package me.dio.hiokdev.reactiveflashcards.api.exceptionhandler.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebExchangeBindHandler extends AbstractHandleException<WebExchangeBindException> {

    private final MessageSource messageSource;

    public WebExchangeBindHandler(final ObjectMapper objectMapper, final MessageSource messageSource) {
        super(objectMapper);
        this.messageSource = messageSource;
    }

    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final WebExchangeBindException exception) {
        return Mono.fromCallable(() -> {
                    prepareExchange(exchange, HttpStatus.BAD_REQUEST);
                    return BaseErrorMessage.GENERIC_BAD_REQUEST.getMessage();
                })
                .map(message -> buildError(HttpStatus.BAD_REQUEST, message))
                .flatMap(problemResponse -> buildParamErrorMessage(problemResponse, exception))
                .doFirst(() -> log.error("==== WebExchangeBindException ", exception))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<ProblemResponse> buildParamErrorMessage(
            final ProblemResponse problemResponse,
            final WebExchangeBindException exception
    ) {
        return Flux.fromIterable(exception.getAllErrors())
                .map(objectError -> ErrorFieldResponse.builder()
                        .name(objectError instanceof FieldError fieldError
                                ? fieldError.getField()
                                : objectError.getObjectName())
                        .message(messageSource.getMessage(objectError, LocaleContextHolder.getLocale()))
                        .build()
                )
                .collectList()
                .map(errorFieldResponseList -> problemResponse.toBuilder()
                        .fields(errorFieldResponseList)
                        .build());
    }

}
