package me.dio.hiokdev.reactiveflashcards.api.controller.deck;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.DeckController;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.DeckResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.DeckMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.service.DeckService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckQueryService;
import me.dio.hiokdev.reactiveflashcards.utils.request.RequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebFluxTest(DeckController.class)
@ContextConfiguration(classes = {DeckMapperImpl.class})
public class DeckControllerFindAllTest extends AbstractControllerTest {

    @MockBean
    public DeckService deckService;
    @MockBean
    public DeckQueryService deckQueryService;
    private RequestBuilder<DeckResponse> deckResponseRequestBuilder;

    @BeforeEach
    void setup() {
        deckResponseRequestBuilder = RequestBuilder
                .deckResponseRequestBuilder(applicationContext, "/decks");
    }

    private static Stream<Arguments> findAllTest() {
        var decks = Stream.generate(() -> DeckDocumentFactoryBot.builder().build())
                .limit(faker.number().randomDigitNotZero())
                .toList();
        Consumer<List<DeckResponse>> assertNonEmpty = response -> {
            assertThat(response).isNotNull();
            assertThat(response).isNotEmpty();
        };
        Consumer<List<DeckResponse>> assertEmpty = response -> {
            assertThat(response).isNotNull();
            assertThat(response).isEmpty();
        };

        return Stream.of(
                Arguments.of(Flux.fromIterable(decks), assertNonEmpty),
                Arguments.of(Flux.empty(), assertEmpty)
        );
    }

    @MethodSource
    @ParameterizedTest
    void findAllTest(final Flux<DeckDocument> deckDocumentMock, final Consumer<List<DeckResponse>> asserts) {
        when(deckQueryService.findAll()).thenReturn(deckDocumentMock);
        deckResponseRequestBuilder.uri(UriBuilder::build)
                .generateRequestWithCollectionBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(asserts);
    }

}
