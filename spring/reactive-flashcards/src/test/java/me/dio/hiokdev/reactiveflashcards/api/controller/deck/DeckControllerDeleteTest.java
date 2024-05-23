package me.dio.hiokdev.reactiveflashcards.api.controller.deck;

import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.DeckController;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.DeckMapperImpl;
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
public class DeckControllerDeleteTest extends AbstractControllerTest {

    @MockBean
    public DeckService deckService;
    @MockBean
    public DeckQueryService deckQueryService;
    private RequestBuilder<Void> noContentRequestBuilder;
    private RequestBuilder<ProblemResponse> problemResponseRequestBuilder;

    @BeforeEach
    void setup() {
        noContentRequestBuilder = RequestBuilder
                .noContentRequestBuilder(applicationContext, "/decks");
        problemResponseRequestBuilder = RequestBuilder
                .problemResponseRequestBuilder(applicationContext, "/decks");
    }

    @Test
    void deleteTest() {
        var deckId = ObjectId.get().toString();
        when(deckService.delete(anyString())).thenReturn(Mono.empty());
        noContentRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(deckId))
                .generateRequestWithoutBody()
                .doDelete()
                .httpStatusIsNoContent();
    }

    @Test
    void whenTryToDeleteNonStoredDeckThenReturnNotFound() {
        var deckId = ObjectId.get().toString();
        when(deckService.delete(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder.pathSegment("{id}").build(deckId))
                .generateRequestWithSimpleBody()
                .doDelete()
                .httpStatusIsNotFound()
                .assertBody(response ->{
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Test
    void whenTryUseInvalidIdThenReturnBadRequest() {
        problemResponseRequestBuilder.uri(uriBuilder -> uriBuilder
                        .pathSegment("{id}")
                        .build(faker.lorem().word()))
                .generateRequestWithSimpleBody()
                .doDelete()
                .httpStatusIsBadRequest()
                .assertBody(response ->{
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(response.fields().stream().map(ErrorFieldResponse::name).toList()).contains("id");
                });
    }

}
