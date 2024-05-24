package me.dio.hiokdev.reactiveflashcards.api.controller.deck;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.DeckController;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.DeckRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.DeckResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.DeckMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.request.DeckRequestFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.service.DeckService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckQueryService;
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

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(DeckController.class)
@ContextConfiguration(classes = {DeckMapperImpl.class})
public class DeckControllerUpdateTest extends AbstractControllerTest {

    @MockBean
    public DeckService deckService;
    @MockBean
    public DeckQueryService deckQueryService;
    private RequestBuilder<DeckResponse> deckResponseRequestBuilder;
    private RequestBuilder<ProblemResponse> problemResponseRequestBuilder;

    @BeforeEach
    void setup() {
        deckResponseRequestBuilder = RequestBuilder
                .deckResponseRequestBuilder(applicationContext, "/decks");
        problemResponseRequestBuilder = RequestBuilder
                .problemResponseRequestBuilder(applicationContext, "/decks");
    }

    @Test
    void updateTest() {
        when(deckService.update(any(DeckDocument.class))).thenAnswer(invocationOnMock -> {
            var documento = invocationOnMock.getArgument(0, DeckDocument.class);
            return Mono.just(documento.toBuilder()
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        var deckId = ObjectId.get().toString();
        var requestBody = DeckRequestFactoryBot.builder().build();
        deckResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(deckId))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPut()
                .httpStatusIsOk()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response).usingRecursiveComparison()
                            .ignoringFields("id")
                            .isEqualTo(requestBody);
                });
    }

    @Test
    void whenTryUpdateNonStoredUserThenReturnNotFound() {
        when(deckService.update(any(DeckDocument.class))).thenReturn(Mono.error(new NotFoundException("")));

        var deckId = ObjectId.get().toString();
        var requestBody = DeckRequestFactoryBot.builder().build();
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(deckId))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPut()
                .httpStatusIsNotFound()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    private static Stream<Arguments> checkConstraintsTest() {
        var validId = ObjectId.get().toString();
        var invalidId = faker.lorem().word();
        return Stream.of(
                Arguments.of(invalidId, DeckRequestFactoryBot.builder().build(), "id"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().blankName().build(), "name"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().longName().build(), "name"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().blankDescription().build(), "description"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().longDescription().build(), "description"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().nullCards().build(), "cards"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().lessThanThreeCards().build(), "cards"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().cardWithBlankFront().build(), "cards[].front"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().cardWithLongFront().build(), "cards[].front"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().cardWithBlankBack().build(), "cards[].back"),
                Arguments.of(validId, DeckRequestFactoryBot.builder().cardWithLongBack().build(), "cards[].back")
        );
    }

    @MethodSource
    @ParameterizedTest
    void checkConstraintsTest(final String deckId, final DeckRequest requestBody, final String field) {
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(deckId))
                .body(requestBody)
                .generateRequestWithSimpleBody()
                .doPut()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(response.fields().stream().map(ErrorFieldResponse::name).toList()).contains(field);
                });
    }

}
