package me.dio.hiokdev.reactiveflashcards.domain.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.core.retry.RetryConfig;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.exception.RetryException;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryHelper {

    private final RetryConfig retryConfig;

    public Retry processRetry(final String retryIdentifier, final Predicate<? super  Throwable> errorFilter) {
        return Retry.backoff(retryConfig.maxRetries(), retryConfig.minDurationSeconds())
                .filter(errorFilter)
                .doBeforeRetry(retrySignal -> log.warn("==== Retrying {} - {} times(s)", retryIdentifier,
                        retrySignal.totalRetries()))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new RetryException(BaseErrorMessage
                        .GENERIC_MAX_RETRIES.getMessage(), retrySignal.failure()));
    }

}
