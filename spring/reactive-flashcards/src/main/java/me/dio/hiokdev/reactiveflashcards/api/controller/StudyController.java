package me.dio.hiokdev.reactiveflashcards.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dio.hiokdev.reactiveflashcards.api.controller.documentation.StudyControllerDoc;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.AnswerQuestionRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.StudyRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.AnswerQuestionResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.QuestionResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.StudyResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.StudyMapper;
import me.dio.hiokdev.reactiveflashcards.core.validation.MongoId;
import me.dio.hiokdev.reactiveflashcards.domain.service.StudyService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.StudyQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RestController
@RequestMapping("studies")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StudyController implements StudyControllerDoc {

    private final StudyService studyService;
    private final StudyQueryService studyQueryService;
    private final StudyMapper studyMapper;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<QuestionResponse> start(@RequestBody @Valid final StudyRequest requestBody) {
        return studyService.start(studyMapper.toDocument(requestBody))
                .doFirst(() -> log.info("==== Try to create a study with follow request {}", requestBody))
                .map(studyDocument -> studyMapper
                        .toResponse(studyDocument.getLastPendingQuestion(), studyDocument.id()));
    }

    @Override
    @GetMapping(value = "{id}/current-question", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<QuestionResponse> getCurrentQuestion(
            @PathVariable @Valid @MongoId(message = "{studyController.id}") final String id
    ) {
        return studyQueryService.getLastPendingQuestion(id)
                .doFirst(() -> log.info("==== Try to get a next question in study {}", id))
                .map(question -> studyMapper.toResponse(question, id));
    }

    @Override
    @GetMapping(value = "user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<StudyResponse> listByUserId(
            @PathVariable @Valid @MongoId(message = "{userController.id}") final String userId
    ) {
        return studyQueryService.findAllByUserId(userId)
                .doFirst(() -> log.info("==== Finding all user studies with a follow user id {}", userId))
                .map(studyMapper::toResponse);
    }

    @Override
    @PostMapping(value = "{id}/answer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AnswerQuestionResponse> answer(
            @PathVariable @Valid @MongoId(message = "{studyController.id}") final String id,
            @RequestBody @Valid AnswerQuestionRequest requestBody
    ) {
        return studyService.answer(id, requestBody.answer())
                .doFirst(() -> log
                        .info("==== Try to answer pending question in study {} with {}", id, requestBody.answer()))
                .map(studyDocument -> studyMapper.toResponse(studyDocument.getLastAnsweredQuestion()));
    }

}
