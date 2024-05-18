package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserPageRequest;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import me.dio.hiokdev.reactiveflashcards.domain.dto.UserPageDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.BaseErrorMessage;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.repository.UserRepository;
import me.dio.hiokdev.reactiveflashcards.domain.repository.UserRepositoryImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserRepositoryImpl userRepositoryImpl;

    public Mono<UserDocument> findById(final String id) {
        return userRepository.findById(id)
                .doFirst(() -> log.info("==== Try to find user with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(BaseErrorMessage
                        .USER_NOT_FOUND.params("id", id).getMessage()))));
    }

    public Mono<UserDocument> findByEmail(final String email) {
        return userRepository.findByEmail(email)
                .doFirst(() -> log.info("==== Try to find user with email {}", email))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(BaseErrorMessage
                        .USER_NOT_FOUND.params("email", email).getMessage()))));
    }

    public Mono<UserPageDocument> findOnDemand(final UserPageRequest request) {
        return userRepositoryImpl.findOnDemand(request)
                .collectList()
                .zipWhen(userDocuments -> userRepositoryImpl.count(request))
                .map(tuple -> UserPageDocument.builder()
                        .limit(request.limit())
                        .currentPage(request.page())
                        .totalItens(tuple.getT2())
                        .content(tuple.getT1())
                        .build()
                );

    }

}
