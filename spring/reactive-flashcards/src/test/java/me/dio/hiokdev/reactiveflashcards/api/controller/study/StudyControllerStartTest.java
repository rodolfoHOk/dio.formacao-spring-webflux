package me.dio.hiokdev.reactiveflashcards.api.controller.study;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.StudyController;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.StudyRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.QuestionResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.StudyMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.StudyDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.request.StudyRequestFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.DeckInStudyException;
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
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(StudyController.class)
@ContextConfiguration(classes = {StudyMapperImpl.class})
public class StudyControllerStartTest extends AbstractControllerTest {

    @MockBean
    private StudyService studyService;
    @MockBean
    private StudyQueryService studyQueryService;
    private RequestBuilder<QuestionResponse> questionResponseRequestBuilder;
    private RequestBuilder<ProblemResponse> problemResponseRequestBuilder;

    @BeforeEach
    void setup() {
        questionResponseRequestBuilder = RequestBuilder
                .questionResponseRequestBuilder(applicationContext, "/studies");
        problemResponseRequestBuilder = RequestBuilder
                .problemResponseRequestBuilder(applicationContext, "/studies");
    }

    @Test
    void startTest() {
        var userId = ObjectId.get().toString();
        var deck = DeckDocumentFactoryBot.builder().build();
        var study = StudyDocumentFactoryBot.builder(userId, deck).pendingQuestions(1).build();
        when(studyService.start(any(StudyDocument.class))).thenReturn(Mono.just(study));

        var requestBody = StudyRequestFactoryBot.builder().build();
        questionResponseRequestBuilder.uri(UriBuilder::build)
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsCreated()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.studyId()).isEqualTo(study.id());
                    assertThat(response.asked()).isEqualTo(study.getLastPendingQuestion().asked());
                    assertThat(response.askedIn()).isEqualTo(study.getLastPendingQuestion().askedIn());
                });
    }

    @Test
    void whenHasOtherStudyWithSameDeckForUserThenReturnConflict() {
        when(studyService.start(any(StudyDocument.class))).thenReturn(Mono.error(new DeckInStudyException("")));

        var requestBody = StudyRequestFactoryBot.builder().build();
        problemResponseRequestBuilder.uri(UriBuilder::build)
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsConflict()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.CONFLICT.value());
                });
    }

    @Test
    void whenTryToStartStudyWithoutStoredDeckOrUSerThenReturnNotFound() {
        when(studyService.start(any(StudyDocument.class))).thenReturn(Mono.error(new NotFoundException("")));

        var requestBody = StudyRequestFactoryBot.builder().build();
        problemResponseRequestBuilder.uri(UriBuilder::build)
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsNotFound()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    private static Stream<Arguments> checkConstraintsTest() {
        return Stream.of(
                Arguments.of(StudyRequestFactoryBot.builder().invalidUserId().build(), "userId"),
                Arguments.of(StudyRequestFactoryBot.builder().invalidDeckId().build(), "deckId")
        );
    }

    @MethodSource
    @ParameterizedTest
    void checkConstraintsTest(final StudyRequest requestBody, final String errorField) {
        problemResponseRequestBuilder.uri(UriBuilder::build)
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPost()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(response.fields().stream().map(ErrorFieldResponse::name).toList()).contains(errorField);
                });
    }

}
