package me.dio.hiokdev.reactiveflashcards.api.controller.study;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.StudyController;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.AnswerQuestionRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.AnswerQuestionResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.StudyMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.StudyDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.request.AnswerQuestionRequestFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.service.StudyService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.StudyQueryService;
import me.dio.hiokdev.reactiveflashcards.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(StudyController.class)
@ContextConfiguration(classes = {StudyMapperImpl.class})
public class StudyControllerAnswerTest extends AbstractControllerTest {

    @MockBean
    private StudyService studyService;
    @MockBean
    private StudyQueryService studyQueryService;
    private RequestBuilder<AnswerQuestionResponse> answerQuestionResponseRequestBuilder;
    private RequestBuilder<ProblemResponse> problemResponseRequestBuilder;

    @BeforeEach
    void setup() {
        answerQuestionResponseRequestBuilder = RequestBuilder
                .answerQuestionResponseRequestBuilder(applicationContext, "/studies");
        problemResponseRequestBuilder = RequestBuilder
                .problemResponseRequestBuilder(applicationContext, "/studies");
    }

    @Test
    void answerTest() {
        var userId = ObjectId.get().toString();
        var deck = DeckDocumentFactoryBot.builder().build();
        var study = StudyDocumentFactoryBot.builder(userId, deck).pendingQuestions(1).build();
        when(studyService.answer(anyString(), anyString())).thenReturn(Mono.just(study));

        var requestBody = AnswerQuestionRequestFactoryBot.builder().build();
        answerQuestionResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .pathSegment("answer")
                        .build(study.id()))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsOk()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    var question = study.getLastAnsweredQuestion();
                    assertThat(response.asked()).isEqualTo(question.asked());
                    assertThat(response.askedIn()).isEqualTo(question.askedIn());
                    assertThat(response.answered()).isEqualTo(question.answered());
                    assertThat(response.answeredIn()).isEqualTo(question.answeredIn());
                    assertThat(response.expected()).isEqualTo(question.expected());
                });
    }

    @Test
    void whenNotFoundStudyOrStudyIsFinishedThenReturnNotFound() {
        when(studyService.answer(anyString(), anyString())).thenReturn(Mono.error(new NotFoundException("")));

        var studyId = ObjectId.get().toString();
        var requestBody = AnswerQuestionRequestFactoryBot.builder().build();
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .pathSegment("answer")
                        .build(studyId))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsNotFound()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    private static Stream<Arguments> checkConstraintTest() {
        var invalidId = faker.lorem().word();
        var validId = ObjectId.get().toString();
        return Stream.of(
                Arguments.of(
                        invalidId,
                        AnswerQuestionRequestFactoryBot.builder().build(),
                        "id"
                ),
                Arguments.of(
                        validId,
                        AnswerQuestionRequestFactoryBot.builder().blankAnswer().build(),
                        "answer"
                ),
                Arguments.of(
                        validId,
                        AnswerQuestionRequestFactoryBot.builder().longAnswer().build(),
                        "answer"
                )
        );
    }

    @MethodSource
    @ParameterizedTest
    void checkConstraintTest(final String studyId, final AnswerQuestionRequest requestBody, final String field) {
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .pathSegment("answer")
                        .build(studyId))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(response.fields().stream().map(ErrorFieldResponse::name)).contains(field);
                });
    }

}
