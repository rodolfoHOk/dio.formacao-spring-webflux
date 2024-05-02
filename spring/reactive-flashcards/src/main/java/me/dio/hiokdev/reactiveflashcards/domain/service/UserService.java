package me.dio.hiokdev.reactiveflashcards.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import me.dio.hiokdev.reactiveflashcards.domain.repository.UserRepository;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryService userQueryService;

    public Mono<UserDocument> save(final UserDocument userDocument) {
        return userRepository.save(userDocument)
                .doFirst(() -> log.info("==== Try to save a follow user {}", userDocument));
    }

    public Mono<UserDocument> update(final UserDocument userDocument) {
        return userQueryService.findById(userDocument.id())
                .map(user -> userDocument.toBuilder()
                        .createdAt(user.createdAt())
                        .updatedAt(user.updatedAt())
                        .build())
                .flatMap(userRepository::save)
                .doFirst(() -> log.info("==== Try to update a user with follow info {}", userDocument));
    }

    public Mono<Void> delete(final String id) {
        return userQueryService.findById(id)
                .flatMap(userRepository::delete)
                .doFirst(() -> log.info("==== Try to delete a user with follow id {}", id));
    }

}
