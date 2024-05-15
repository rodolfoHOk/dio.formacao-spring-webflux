package me.dio.hiokdev.reactiveflashcards.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.documentation.UserControllerDoc;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserPageRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserPageResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.UserMapper;
import me.dio.hiokdev.reactiveflashcards.core.validation.MongoId;
import me.dio.hiokdev.reactiveflashcards.domain.service.UserService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
public class UserController implements UserControllerDoc {

    private final UserService userService;
    private final UserQueryService userQueryService;
    private final UserMapper userMapper;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponse> save(@Valid @RequestBody final UserRequest requestBody) {
        return userService.save(userMapper.toDocument(requestBody))
                .doFirst(() -> log.info("==== Saving a user with follow data {}", requestBody))
                .map(userMapper::toResponse);
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserPageResponse> findOnDemand(@Valid final UserPageRequest request) {
        return userQueryService.findOnDemand(request)
                .doFirst(() -> log.info("==== Finding users on demand with follow request {}", request))
                .map(userPageDocument -> userMapper.toResponse(userPageDocument, request.limit()));
    }

    @Override
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponse> findById(@PathVariable @Valid @MongoId(message = "{userController.id}") final String id) {
        return userQueryService.findById(id)
                .doFirst(() -> log.info("==== Finding a user with follow id {}", id))
                .map(userMapper::toResponse);
    }

    @Override
    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponse> update(
            @PathVariable @Valid @MongoId(message = "{userController.id}") final String id,
            @RequestBody @Valid final UserRequest requestBody
    ) {
        return userService.update(userMapper.toDocument(requestBody, id))
                .doFirst(() -> log.info("==== Updating a user with follow info [body: {}, id: {}]", requestBody, id))
                .map(userMapper::toResponse);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "{id}")
    public Mono<Void> delete(@PathVariable @Valid @MongoId(message = "{userController.id}") final String id) {
        return userService.delete(id)
                .doFirst(() -> log.info("==== Deleting a user with follow id {}", id));
    }

}
