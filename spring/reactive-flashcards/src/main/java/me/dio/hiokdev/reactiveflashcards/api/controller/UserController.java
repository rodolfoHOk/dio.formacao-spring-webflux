package me.dio.hiokdev.reactiveflashcards.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.UserMapper;
import me.dio.hiokdev.reactiveflashcards.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RestController
@RequestMapping("users")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponse> save(@Valid @RequestBody final UserRequest request) {
        return userService.save(userMapper.toDocument(request))
                .doFirst(() -> log.info("==== Saving a user with follow data {}", request))
                .map(userMapper::toResponse);
    }

}