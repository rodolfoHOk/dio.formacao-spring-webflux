package me.dio.hiokdev.reactiveflashcards.api.controller.deck;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.DeckController;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.DeckResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.DeckMapperImpl;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.domain.exception.NotFoundException;
import me.dio.hiokdev.reactiveflashcards.domain.service.DeckService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.DeckQueryService;
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

@WebFluxTest(DeckController.class)
@ContextConfiguration(classes = {DeckMapperImpl.class})
public class DeckControllerFindByIdTest extends AbstractControllerTest {

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
    void findByIdTest() {
        var deck = DeckDocumentFactoryBot.builder().build();
        when(deckQueryService.findById(anyString())).thenReturn(Mono.just(deck));
        deckResponseRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(deck.id()))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsOk()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(deck);
                });
    }

    @Test
    void whenTryToFindNonStoredDeckThenReturnNotFound() {
        when(deckQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var deckId = ObjectId.get().toString();
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(deckId))
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
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(invalidId))
                .generateRequestWithSimpleBody()
                .doGet()
                .httpStatusIsBadRequest()
                .assertBody(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

}
