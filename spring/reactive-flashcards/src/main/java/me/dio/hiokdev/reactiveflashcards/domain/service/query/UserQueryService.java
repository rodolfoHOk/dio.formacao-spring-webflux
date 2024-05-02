package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public Mono<UserDocument> findById(final String id) {
        return userRepository.findById(id)
                .doFirst(() -> log.info("==== try to find user with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(BaseErrorMessage
                        .USER_NOT_FOUND.params("id", id).getMessage()))));
    }

}
