package me.dio.hiokdev.reactiveflashcards.api.controller.study;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.StudyController;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.StudyResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.StudyMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.StudyDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import me.dio.hiokdev.reactiveflashcards.domain.service.StudyService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.StudyQueryService;
import me.dio.hiokdev.reactiveflashcards.utils.request.RequestBuilder;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(StudyController.class)
@ContextConfiguration(classes = {StudyMapperImpl.class})
public class StudyControllerListByUserIdTest extends AbstractControllerTest {

    @MockBean
    private StudyService studyService;
    @MockBean
    private StudyQueryService studyQueryService;
    private RequestBuilder<StudyResponse> studyResponseRequestBuilder;

    @BeforeEach
    void setup() {
        studyResponseRequestBuilder = RequestBuilder
                .studyResponseRequestBuilder(applicationContext, "/studies");
    }

    private static Stream<Arguments> listByUserIdTest() {
        var userId = ObjectId.get().toString();
        var randomSize = faker.number().randomDigitNotZero();
        var decks = Stream.generate(() -> DeckDocumentFactoryBot.builder().build()).limit(randomSize).toList();
        var studies = decks.stream().map(deck -> StudyDocumentFactoryBot.builder(userId, deck).build()).toList();
        Consumer<List<StudyResponse>> assertNonEmpty = response -> {
            assertThat(response).isNotNull();
            assertThat(response).isNotEmpty();
        };
        Consumer<List<StudyResponse>> assertEmpty = response -> {
            assertThat(response).isNotNull();
            assertThat(response).isEmpty();
        };

        return Stream.of(
                Arguments.of(userId, Flux.fromIterable(studies), assertNonEmpty),
                Arguments.of(userId, Flux.empty(), assertEmpty)
        );
    }

    @MethodSource
    @ParameterizedTest
    void listByUserIdTest(final String userId, final Flux<StudyDocument> studyDocumentMock, final Consumer<List<StudyResponse>> asserts) {
        when(studyQueryService.findAllByUserId(anyString())).thenReturn(studyDocumentMock);
        studyResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("user")
                        .pathSegment("{userId}")
                        .build(userId))
                .generateRequestWithCollectionBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(asserts);
    }

}
