package me.dio.hiokdev.reactiveflashcards.api.controller.study;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.StudyController;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.QuestionResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.StudyMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.StudyDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.service.StudyService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.StudyQueryService;
import me.dio.hiokdev.reactiveflashcards.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(StudyController.class)
@ContextConfiguration(classes = {StudyMapperImpl.class})
public class StudyControllerGetCurrentQuestionTest extends AbstractControllerTest {

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
    void getCurrentQuestionTest() {
        var userId = ObjectId.get().toString();
        var deck = DeckDocumentFactoryBot.builder().build();
        var question = StudyDocumentFactoryBot.builder(userId, deck)
                .pendingQuestions(1)
                .build()
                .getLastPendingQuestion();
        when(studyQueryService.getLastPendingQuestion(anyString())).thenReturn(Mono.just(question));
        var studyId = ObjectId.get().toString();
        questionResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .pathSegment("current-question")
                        .build(studyId))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.studyId()).isEqualTo(studyId);
                    assertThat(response.asked()).isEqualTo(question.asked());
                });
    }

    @Test
    void whenTryToGetQuestionFromNonStoredStudyThenReturnNotFound() {
        when(studyQueryService.getLastPendingQuestion(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var studyId = ObjectId.get().toString();
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .pathSegment("current-question")
                        .build(studyId))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsNotFound()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Test
    void whenTryUseInvalidIdThenReturnBadRequest() {
        var invalidId = faker.lorem().word();
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .pathSegment("current-question")
                        .build(invalidId))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

}
